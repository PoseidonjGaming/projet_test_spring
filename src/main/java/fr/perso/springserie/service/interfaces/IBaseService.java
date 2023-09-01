package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;

import java.util.List;

public interface IBaseService<E extends BaseEntity, D extends BaseDTO> {
    List<D> getAll();

    D getById(int id);

    void save(D d);

    void delete(int id);
}
