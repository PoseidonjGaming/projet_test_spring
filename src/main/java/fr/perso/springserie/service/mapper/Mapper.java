package fr.perso.springserie.service.mapper;

import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import static fr.perso.springserie.service.utility.ServiceUtility.browseField;

@Service
@Transactional
@Primary
public class Mapper implements IMapper {

    private final MapService mapService;

    public Mapper(MapService mapService) {
        this.mapService = mapService;
    }

    @Override
    public <S, T> T convert(S source, Class<T> targetClass) {

        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            browseField(source.getClass(), target, (field, object) -> map(source.getClass(), source, object, field));
            return target;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }


    private <S, T> void map(Class<?> sourceClass, S source, T target, Field sourceField) {
        if (sourceField.getType().isAnnotationPresent(Entity.class)) {
            mapEntityId(source, target, sourceField);
        } else if (sourceField.getType().equals(List.class)) {
            mapList(source, target, sourceField);
        } else if (sourceField.getType().equals(int.class) && sourceField.getName().endsWith("Id")) {
            mapEntity(source, target, sourceField);
        } else if (sourceField.isAnnotationPresent(Embedded.class)) {
            mapEmbedded(source, target, sourceField);
         } else {
            transfert(source, target, sourceField, target.getClass());
        }
        Arrays.stream(target.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Embedded.class)).forEach(targetField -> {
            try {
                Object embedded = targetField.getType().getDeclaredConstructor().newInstance();
                browseField(source.getClass(), field -> map(sourceClass, source, embedded, field));
                set(embedded, target, targetField);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        Class<?> superClass = sourceClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            browseField(superClass, target, (superField, o) -> map(superClass, source, o, superField));
        }
    }

    private <S, T> void mapEmbedded(S source, T target, Field sourceField) {
        Object embedded = get(sourceField, source);
        browseField(sourceField.getType(), field -> map(source.getClass(), embedded, target, field));
    }

    private <S, T> void mapEntity(S source, T target, Field sourceField) {
        String entityName = sourceField.getName().substring(0, sourceField.getName().length() - 2);
        Field targetField = getField(entityName, target.getClass());
        if (targetField != null) {
            mapService.getRepo(targetField.getType().getSimpleName().toLowerCase()).findById(get(sourceField, source)).ifPresent(entity -> {
                set(entity, target, targetField);
            });
        }
    }

    private <S, T> void mapEntityId(S source, T target, Field sourceField) {
        Field targetField = getField(sourceField.getName().concat("Id"), target.getClass());
        if (targetField != null) {
            BaseEntity entity = get(sourceField, source);
            if (entity != null)
                set(entity.getId(), target, targetField);
        }
    }

    private <S, T> void mapList(S source, T target, Field sourceField) {
        Class<?> listType = (Class<?>) ((ParameterizedType) sourceField.getGenericType()).getActualTypeArguments()[0];
        if (listType.isAnnotationPresent(Entity.class)) {
            Field targetField = getField(sourceField.getName().concat("Ids"), target.getClass());
            if (targetField != null) {
                List<BaseEntity> entities = get(sourceField, source);
                if (entities != null)
                    set(entities.stream().map(BaseEntity::getId).toList(), target, targetField);
            }

        } else {
            Field targetField = getField(sourceField.getName().substring(0, (sourceField.getName().length() - 3)), target.getClass());
            if (targetField != null && isMapped(targetField)) {
                List<Integer> ids = get(sourceField, source);
                if (ids != null)
                    set(mapService.getRepo(targetField.getName()).findByIdIn(ids), target, targetField);
            }
        }
    }

    private boolean isMapped(Field targetField) {
        return (targetField.isAnnotationPresent(OneToMany.class) && targetField.getAnnotation(OneToMany.class).mappedBy().isEmpty()) ||
                (targetField.isAnnotationPresent(ManyToMany.class) && targetField.getAnnotation(ManyToMany.class).mappedBy().isEmpty());
    }


    private <S, T> void transfert(S source, T target, Field sourceField, Class<?> targetClass) {
        if (source.getClass().getSuperclass().equals(sourceField.getDeclaringClass()) || source.getClass().equals(sourceField.getDeclaringClass())) {
            Field targetField = getField(sourceField.getName(), targetClass);
            if (targetField != null) {
                set(get(sourceField, source), target, targetField);
            } else if (!targetClass.getSuperclass().equals(Object.class)) {
                transfert(source, target, sourceField, targetClass.getSuperclass());
            }
        }

    }


    protected <O> O get(Field field, Object object) {
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

    protected <O, T> void set(O source, T target, Field targetField) {
        try {
            targetField.setAccessible(true);
            targetField.set(target, source);
            targetField.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected <O> Field getField(String name, Class<O> sourceClass) {
        if (sourceClass != null) {
            try {
                return sourceClass.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                return null;
            }
        }
        return null;


    }
}
