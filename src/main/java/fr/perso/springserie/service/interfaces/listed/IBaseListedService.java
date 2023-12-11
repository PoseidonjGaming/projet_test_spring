package fr.perso.springserie.service.interfaces.listed;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;

import java.util.List;

public interface IBaseListedService<D extends BaseDTO> {
    List<D> getAll();

    List<D> getByIds(List<Integer> ids);

    List<D> search(SearchDTO<D> searchDto);

    List<D> sort(SortDTO sortDTO);

    List<D> sortSearch(SearchDTO<D> searchDto, SortDTO sortDTO);
}
