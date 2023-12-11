package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.service.interfaces.ICRUDService;
import fr.perso.springserie.service.interfaces.listed.IBaseListedService;
import fr.perso.springserie.service.interfaces.paged.IBasePagedService;

public interface IBaseService<D extends BaseDTO> extends ICRUDService<D>, IBasePagedService<D>, IBaseListedService<D> {
}
