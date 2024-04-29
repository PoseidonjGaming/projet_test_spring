package fr.perso.springserie.service.mapper;

import fr.perso.springserie.interceptor.exception.GenericException;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.service.MapService;
import fr.perso.springserie.utility.annotation.Entity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static fr.perso.springserie.service.utility.ServiceUtility.*;

@Service
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
            throw new GenericException(e);
        }
    }


    private <S, T> void map(Class<?> sourceClass, S source, T target, Field sourceField) {
        if (sourceField.getType().isAnnotationPresent(Entity.class)) {
            mapEntityId(source, target, sourceField);
        } else if (sourceField.getType().equals(List.class)) {
            mapList(source, target, sourceField);
        } else {
            transfert(source, target, sourceField, target.getClass());
        }

        Class<?> superClass = sourceClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            browseField(superClass, target, (superField, o) -> map(superClass, source, o, superField));
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

        } else if (sourceField.getName().endsWith("Ids")) {
            Field targetField = getField(sourceField.getName().substring(0, (sourceField.getName().length() - 3)), target.getClass());
            if (targetField != null) {
                List<Integer> ids = get(sourceField, source);
                if (ids != null)
                    set(mapService.getRepo(targetField.getName()).findByIdIn(ids), target, targetField);

            }
        } else {
            transfert(source, target, sourceField, target.getClass());
        }
    }


    private <S, T> void transfert(S source, T target, Field sourceField, Class<?> targetClass) {
        if (source.getClass().getSuperclass().equals(sourceField.getDeclaringClass())
                || source.getClass().equals(sourceField.getDeclaringClass())) {
            Field targetField = getField(sourceField.getName(), targetClass);
            if (targetField != null) {
                set(get(sourceField, source), target, targetField);
            } else if (!targetClass.getSuperclass().equals(Object.class)) {
                transfert(source, target, sourceField, targetClass.getSuperclass());
            }
        }

    }
}
