package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToMany;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Service
@Transactional
public class Mapper<E extends BaseEntity, D extends BaseDTO> implements IMapper<E, D> {

    private final ModelMapper mapper;
    private final MapService mapService;

    public Mapper(MapService mapService) {
        this.mapService = mapService;
        this.mapper = new ModelMapper();
    }

    @Override
    public E toEntity(D dto, Class<E> entityClass) {
        E entity = mapper.map(dto, entityClass);
        browseField(entityClass, entity, (field, object) -> mapToEntity(dto, field, object));
        return entity;
    }


    @Override
    public D toDTO(E entity, Class<D> dtoClass, Class<E> entityClass) {
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

    @Override
    public List<D> toDTOList(List<E> entities, Class<D> dtoClass) {
        return entities.stream().map(entity -> toDTO(entity, dtoClass, (Class<E>) entity.getClass())).toList();
    }

    @Override
    public <O> void browseField(Class<?> clazz, O object, BiConsumer<Field, O> consumer) {
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> consumer.accept(field, object));
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
            Field dtoField = getField(dtoFieldName, (Class<D>) dto.getClass());
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


    private <O> void mapRelatedEntity(O object, D dto, Field field) throws IllegalAccessException {
        String lowerCase = field.getType().getSimpleName().toLowerCase();
        Class<?> relatedClass = mapService.getClass(lowerCase);
        if (relatedClass != null) {
            field.setAccessible(true);
            Field dtoField = getField(field.getName().concat("Id"), (Class<D>) dto.getClass());
            if (dtoField != null) {
                dtoField.setAccessible(true);
                field.set(object, mapService.getRepo(lowerCase).findById((Integer) dtoField.get(dto)).orElse(null));
                dtoField.setAccessible(false);
            }
            field.setAccessible(false);

        }
    }

    private <O> void mapList(O object, D dto, Field field) throws IllegalAccessException {
        Field dtoField = getField(field.getName().concat("Ids"), (Class<D>) dto.getClass());
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

    private Field getField(String name, Class<D> dtoClass) {
        try {
            return dtoClass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }

    }


    protected <R extends BaseEntity> List<R> getRelatedEntities(List<Integer> ids, IBaseRepo<R> repo) {
        return repo.findByIdIn(ids);
    }
}
