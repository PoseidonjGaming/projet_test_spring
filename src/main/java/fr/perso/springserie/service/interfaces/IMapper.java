package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;

public interface IMapper<E extends BaseEntity, D extends BaseDTO> {
    E toEntity(D dto);

    D toDTO(E entity);
}
