package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.PageRequest;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDateDto;
import fr.perso.springserie.model.dto.special.SearchDto;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public interface IBaseService<D extends BaseDTO> {
    PageRequest<D> getAll();
    PageRequest<D> getAll(int size, int page);

    D getById(int id);

    List<D> getBydIds(List<Integer> ids);

    List<D> search(D dto, SearchDto searchDto, SearchDateDto searchDateDto);
    List<D> sort(String field, Sort.Direction direction);
    List<D> sortSearch(String field, Sort.Direction direction, D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType);

    D save(D d);
    void save(File file);
    void delete(int id);
    void saves(List<D> ds);


}
