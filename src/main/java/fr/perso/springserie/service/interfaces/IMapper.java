package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

public interface IMapper<E extends BaseEntity, D extends BaseDTO> {

    E toEntity(D dto, Class<E> entityClass);

    D toDTO(E entity, Class<D> dtoClass, Class<E> entityClass);

    List<D> toDTOList(List<E> entities, Class<D> dtoClass);

    <O> void browseField(Class<?> clazz, O object, BiConsumer<Field, O> consumer);
}
