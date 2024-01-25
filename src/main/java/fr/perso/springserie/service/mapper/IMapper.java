package fr.perso.springserie.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface IMapper {

    <S, T> T convert(S source, Class<T> targetClass);

    default <S, T> List<T> convertList(List<S> listSource, Class<T> targetClass) {
        return listSource.stream().map(source -> convert(source, targetClass)).collect(Collectors.toList());
    }


}
