package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.interfaces.IMapper;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements IBaseService<E, D>, IMapper<E, D> {

    protected final IBaseRepo<E> repository;
    protected final ModelMapper mapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;

    protected BaseService(IBaseRepo<E> repository, Class<D> dtoClass, Class<E> entityClass) {
        this.repository = repository;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        mapper = new ModelMapper();
    }

    @Override
    public List<D> getAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public D getById(int id) {
        return toDTO(repository.findById(id).orElse(null));
    }

    @Override
    public void save(D d) {
        E entity = toEntity(d);
        System.out.println(d);
        repository.save(entity);
    }

    @Override
    public void delete(int id) {
        repository.delete(toEntity(getById(id)));
    }

    @Override
    public E toEntity(D dto) {
        return mapper.map(dto, entityClass);
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
}
