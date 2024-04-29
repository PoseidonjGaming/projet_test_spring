package fr.perso.springserie.service.interfaces.paged;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.PagedResponse;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;

import java.util.List;

public interface IBasePagedService<D extends BaseDTO> {

    PagedResponse<D> getAll(int size, int page);

    PagedResponse<D> getByIds(List<Integer> ids, int size, int page);

    PagedResponse<D> search(SearchDTO<D> searchDto, int size, int page);

    PagedResponse<D> sort(SortDTO sortDTO, int size, int page);

    PagedResponse<D> sortSearch(SearchDTO<D> searchDto, SortDTO sortDTO, int size, int page);
}
