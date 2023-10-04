package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.BaseDTO;

import java.io.File;
import java.util.List;

public interface IBaseService<D extends BaseDTO> {
    List<D> getAll();

    D getById(int id);

    List<D> getBydIds(List<Integer> ids);

    void save(D d);
    void save(File file);

    void delete(int id);

    List<D> search(String term);
}
