package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.BaseDTO;

import java.io.File;
import java.util.List;

public interface ICRUDService<D extends BaseDTO> {
    D getById(String id);

    D save(D dto);

    void save(File file);

    void saves(List<D> ds);

    void delete(String id);
}
