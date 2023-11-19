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


    private <S, T> void map(S source, T target, Field field) {
        if (field.getAnnotation(Embedded.class) != null) {
            mapEmbedded(source, target, field);
        } else if (field.getType().equals(BaseEntity.class)) {
            mapEntity(source, target, field);
        } else if (field.getType().equals(List.class)) {
            mapEntities(source, target, field);
        } else {
            transfert(source, target, field);
        }
    }

    private <S, T> void mapEntities(S source, T target, Field targetField) {
        if (source instanceof BaseEntity baseEntity) {
            System.out.println("map");
        } else if (source instanceof BaseDTO baseDTO) {

            if (targetField.getAnnotation(ManyToMany.class) != null && targetField.getAnnotation(ManyToMany.class).mappedBy().isEmpty()) {
                System.out.println(targetField.getName());
                List<Integer> ids = get(getField(targetField.getName().concat("Ids"), source.getClass()), source);
                List<BaseEntity> entities = mapService.getRepo(targetField.getName()).findByIdIn(ids);
            } else {
                System.out.println(targetField.getName());
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
            targetField.setAccessible(true);
            targetField.setAccessible(true);
            set(get(sourceField, source), target, targetField);
            targetField.setAccessible(false);
            targetField.setAccessible(false);
        }
    }

    private <S, T> void mapEmbedded(S source, T target, Field field) {
        try {
            Object embeddedObject = field.getType().getDeclaredConstructor().newInstance();
            browseField(embeddedObject.getClass(), embeddedObject, (embeddedField, object) -> map(source, object, embeddedField));
            set(embeddedObject, target, field);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
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
            if (source != null) {
                targetField.setAccessible(true);
                targetField.set(target, source);
                targetField.setAccessible(false);
            }
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
