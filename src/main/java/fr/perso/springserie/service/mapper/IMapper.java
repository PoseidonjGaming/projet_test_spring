package fr.perso.springserie.service.mapper;

import java.util.List;

public interface IMapper {

    <S, T> T convert(S source, Class<T> targetClass);
    <S, T> List<T> convertList(List<S> listSource, Class<T> targetClass);


}
