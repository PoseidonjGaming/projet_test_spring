package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.BaseDTO;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.util.List;

public interface IBaseService<D extends BaseDTO> {
    List<D> getAll();

    D getById(int id);

    List<D> getBydIds(List<Integer> ids);

    List<D> search(D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType);

    D save(D d);

    void save(File file);

    void delete(int id);


    void saves(List<D> ds);

    List<D> order(String field, Sort.Direction direction);
}
