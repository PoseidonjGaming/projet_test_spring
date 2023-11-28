package fr.perso.springserie.service.mapper;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public interface IMapper {

    <S, T> T convert(S source, Class<T> targetClass);
    <S, T> List<T> convertList(List<S> listSource, Class<T> targetClass);


}
