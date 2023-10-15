package fr.perso.springserie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.interfaces.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.ManyToMany;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements IBaseService<D>, IMapper<E, D> {

    protected final IBaseRepo<E> repository;
    protected final ModelMapper mapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;

    @Autowired
    protected MapService mapService;

    protected BaseService(IBaseRepo<E> repository, Class<D> dtoClass, Class<E> entityClass) {
        this.repository = repository;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        mapper = new ModelMapper();
    }

    @Override
    public List<D> getAll() {
        return toDTOList(repository.findAll());
    }

    @Override
    public D getById(int id) {
        return toDTO(repository.findById(id).orElse(null));
    }

    @Override
    public void save(D d) {
        repository.save(toEntity(d));
    }

    @Override
    public void save(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            List<D> dtoList = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, dtoClass));
            dtoList.forEach(this::save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        repository.delete(toEntity(getById(id)));
    }

    @Override
    public E toEntity(D dto) {
        E entity = mapper.map(dto, entityClass);
        Arrays.stream(entityClass.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            try {
                String lowerCase = field.getType().getSimpleName().toLowerCase();
                Class<?> clazz = mapService.getClass(lowerCase);
                Field dtoField;

                if (clazz != null) {
                    dtoField = getField(field.getName().concat("Id"));
                    if (dtoField != null) {
                        dtoField.setAccessible(true);
                        field.set(entity, mapService.getRepo(lowerCase).findById((Integer) dtoField.get(dto)).orElse(null));

                        dtoField.setAccessible(false);
                    }

                }

                if (field.getType().equals(List.class)) {
                    dtoField = getField(field.getName().concat("Ids"));
                    if (dtoField != null) {
                        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                        if (manyToMany != null && manyToMany.mappedBy().isEmpty()) {
                            dtoField.setAccessible(true);
                            List<?> relatedEntities = getRelatedEntities((List<Integer>) dtoField.get(dto), mapService.getRepo(field.getName()));
                            field.set(entity, relatedEntities);
                            dtoField.setAccessible(false);
                        }
                    }


                }

                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return entity;
    }

    private Field getField(String name) {
        try {
            return dtoClass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }

    }

    @Override
    public D toDTO(E entity) {
        return mapList(entity, mapper.map(entity, dtoClass));
    }

    protected <T extends BaseEntity> List<Integer> mapList(List<T> list) {
        return list.stream().map(BaseEntity::getId).toList();
    }

    protected D mapList(E entity, D dto) {
        Arrays.stream(entity.getClass().getDeclaredFields()).filter(e -> e.getType().equals(List.class)).forEach(field -> {
            try {
                field.setAccessible(true);
                List<E> list = (List<E>) field.get(entity);
                field.setAccessible(false);

                Field dtoField = Arrays.stream(dto.getClass().getDeclaredFields()).filter(field1 -> field1.getName().equals(field.getName() + "Ids")).findFirst().orElse(null);
                dtoField.setAccessible(true);
                dtoField.set(dto, list.stream().map(BaseEntity::getId).toList());
                dtoField.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        });
        return dto;
    }

    protected List<D> toDTOList(List<E> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    protected <R extends BaseEntity> List<R> getRelatedEntities(List<Integer> ids, IBaseRepo<R> repo) {
        return repo.findByIdIn(ids);
    }

    @Override
    public List<D> getBydIds(List<Integer> ids) {
        return toDTOList(repository.findByIdIn(ids));
    }
}
