package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.SaisonDTO;
import fr.perso.springserie.model.entity.Saison;

import java.util.List;

public interface ISaisonService extends IBaseService<Saison, SaisonDTO>, IMapper<Saison, SaisonDTO> {
    List<SaisonDTO> getBySeriesId(int id);
}
