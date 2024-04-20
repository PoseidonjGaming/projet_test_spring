package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.service.interfaces.ICRUDService;
import fr.perso.springserie.service.interfaces.listed.IBaseListedService;
import fr.perso.springserie.service.interfaces.paged.IBasePagedService;

import java.util.Map;

public interface IBaseService<D extends BaseDTO> extends ICRUDService<D>, IBasePagedService<D>, IBaseListedService<D> {
    Map<String, String> getStructure();

    Map<String, String> getTypes();

    Map<String, String> getDisplay();
}
