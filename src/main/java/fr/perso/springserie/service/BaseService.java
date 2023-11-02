package fr.perso.springserie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.interfaces.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToMany;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements IBaseService<D>, IMapper<E, D> {

    protected final IBaseRepo<E> repository;
    protected final ModelMapper mapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;

    protected final MapService mapService;

    protected BaseService(IBaseRepo<E> repository, Class<D> dtoClass, Class<E> entityClass, MapService mapService) {
        this.repository = repository;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        this.mapService = mapService;
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
    public List<D> getBydIds(List<Integer> ids) {
        return toDTOList(repository.findByIdIn(ids));
    }

    @Override
    public List<D> search(D dto) {
        final ExampleMatcher[] exampleMatcher = {ExampleMatcher.matchingAny()
                .withIgnoreNullValues().withIgnorePaths("id")};

        Arrays.stream(entityClass.getDeclaredFields()).forEach(field -> exampleMatcher[0] = exampleMatcher[0].withMatcher(field.getName(), matcher -> matcher.contains().ignoreCase()));
        List<E> entities = repository.findAll(Example.of(toEntity(dto), exampleMatcher[0]));
        return toDTOList(entities);
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
        browseField(entityClass, entity, (field, object) -> mapToEntity(dto, field, object));
        return entity;
    }


    @Override
    public D toDTO(E entity) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            dto.setId(entity.getId());
            browseField(entityClass, entity, (field, e) -> mapToDTO(dto, field, e));
            return dto;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private <O> void mapToDTO(D dto, Field field, O object) {
        try {
            if (field.getAnnotation(Embedded.class) != null) {
                field.setAccessible(true);
                browseField(field.getType(), field.get(object), (field1, o) -> mapToDTO(dto, field1, o));
                field.setAccessible(false);
            } else if (field.getType().equals(List.class)) {
                set(dto, field.getName().concat("Ids"), field, () ->
                        ((List<? extends BaseEntity>) get(field, object)).stream().map(BaseEntity::getId).toList());
            } else if (field.getType().getSuperclass().equals(BaseEntity.class)) {
                set(dto, field.getName().concat("Id"), field, () -> {
                    try {
                        field.setAccessible(true);
                        BaseEntity baseEntity = (BaseEntity) field.get(object);
                        field.setAccessible(false);
                        return (baseEntity == null) ? 0 : baseEntity.getId();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else if (field.getType().isPrimitive() || field.getType().equals(String.class)
                    || field.getType().equals(Integer.class)) {
                set(dto, field.getName(), field, () -> {
                    try {
                        field.setAccessible(true);
                        Object o = field.get(object);
                        field.setAccessible(false);
                        return o;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <O> void mapToEntity(D dto, Field field, O object) {
        try {
            if (field.getAnnotation(Embedded.class) != null) {
                mapEmbeddableEntity(object, dto, field);
            } else if (field.getType().equals(List.class)) {
                mapList(object, dto, field);
            } else if (field.getType().getSuperclass().equals(BaseEntity.class)) {
                mapRelatedEntity(object, dto, field);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <O> O get(Field field, Object object) {
        O returned;
        try {
            field.setAccessible(true);
            returned = (O) field.get(object);
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return returned;

    }

    private <O> void set(D dto, String dtoFieldName, Field entityField, Supplier<O> supplier) {

        try {
            Field dtoField = getField(dtoFieldName);
            O object = supplier.get();
            if (dtoField != null && object != null) {
                dtoField.setAccessible(true);
                dtoField.set(dto, object);
                entityField.setAccessible(false);
                dtoField.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private <O> void browseField(Class<?> clazz, O object, BiConsumer<Field, O> consumer) {
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> consumer.accept(field, object));
    }

    private <O> void mapRelatedEntity(O object, D dto, Field field) throws IllegalAccessException {
        String lowerCase = field.getType().getSimpleName().toLowerCase();
        Class<?> relatedClass = mapService.getClass(lowerCase);
        if (relatedClass != null) {
            field.setAccessible(true);
            Field dtoField = getField(field.getName().concat("Id"));
            if (dtoField != null) {
                dtoField.setAccessible(true);
                field.set(object, mapService.getRepo(lowerCase).findById((Integer) dtoField.get(dto)).orElse(null));
                dtoField.setAccessible(false);
            }
            field.setAccessible(false);

        }
    }

    private <O> void mapList(O object, D dto, Field field) throws IllegalAccessException {
        Field dtoField = getField(field.getName().concat("Ids"));
        if (dtoField != null) {
            field.setAccessible(true);
            dtoField.setAccessible(true);
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            Object o = dtoField.get(dto);
            if (manyToMany != null && manyToMany.mappedBy().isEmpty() && o != null) {
                List<?> relatedEntities = getRelatedEntities((List<Integer>) o, mapService.getRepo(field.getName()));
                field.set(object, relatedEntities);
                dtoField.setAccessible(false);
            }
            field.setAccessible(true);
        }
    }

    private <O> void mapEmbeddableEntity(O object, D dto, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Object map = mapper.map(dto, field.getType());
        browseField(field.getType(), map, (field1, o) -> mapToEntity(dto, field1, o));
        field.set(object, map);
        field.setAccessible(false);
    }

    private Field getField(String name) {
        try {
            return dtoClass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }

    }

    protected List<D> toDTOList(List<E> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    protected <R extends BaseEntity> List<R> getRelatedEntities(List<Integer> ids, IBaseRepo<R> repo) {
        return repo.findByIdIn(ids);
    }


}
