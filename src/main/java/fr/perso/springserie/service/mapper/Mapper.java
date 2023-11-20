package fr.perso.springserie.service.mapper;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToMany;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static fr.perso.springserie.service.utility.ServiceUtility.browseField;

@Service
@Transactional
@Primary
public class Mapper implements IMapper {

    private final ModelMapper modelMapper;
    private final MapService mapService;

    public Mapper(MapService mapService) {
        this.mapService = mapService;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public <S, T> T convert(S source, Class<T> targetClass) {
        T target = modelMapper.map(source, targetClass);
        browseField(targetClass, target, (field, object) -> map(source, object, field));
        return target;
    }


    private <S, T> void map(S source, T target, Field targetField) {
        if (targetField.getAnnotation(Embedded.class) != null) {
            mapEmbedded(source, target, targetField);
        } else if (targetField.getType().equals(BaseEntity.class)) {
            mapEntity(source, target, targetField);
        } else if (targetField.getType().equals(List.class)) {
            mapEntities(source, target, targetField);
        } else {
            transfert(source, target, targetField);
        }
    }

    private <S, T> void mapEntities(S source, T target, Field sourceField) {
        if (sourceField.getAnnotation(ManyToMany.class) != null &&
                sourceField.getAnnotation(ManyToMany.class).mappedBy().isEmpty()) {
            Field targetField = getField(sourceField.getName().concat("Ids"),
                    target.getClass());
            set(mapService.getRepo(sourceField.getName()).findByIdIn(
                    get(sourceField, source)),target, targetField);
        } else if (sourceField.getName().endsWith("Ids")) {
            Field targetField = getField(sourceField.getName().substring(0, sourceField.getName().length() - 3), source.getClass());
            if (targetField != null) {
                List<BaseEntity> sourceObject = get(targetField, source);
                set(sourceObject.stream().map(BaseEntity::getId).toList(), target, targetField);
            }

        }
    }

    private <S, T> void mapEntity(S source, T target, Field field) {
        if (source instanceof BaseEntity baseEntity) {
            set(baseEntity.getId(), target, field);
        } else if (source instanceof BaseDTO baseDTO) {
            set(mapService.getRepo(field.getName()).findById(baseDTO.getId()).orElse(null), target, field);
        }
    }

    private <S, T> void transfert(S source, T target, Field targetField) {
        Field sourceField = getField(targetField.getName(), source.getClass());
        if (sourceField != null) {
            set(get(sourceField, source), target, targetField);
        }
    }

    private <S, T> void mapEmbedded(S source, T target, Field field) {
        Object embeddedObject=get(field,target);
        browseField(o);
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
        try {
            return sourceClass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }

    }
}
