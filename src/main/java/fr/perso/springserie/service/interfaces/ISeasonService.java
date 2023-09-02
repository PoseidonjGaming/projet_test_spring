package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.entity.Season;

import java.util.List;

public interface ISeasonService extends IBaseService<Season, SeasonDTO>, IMapper<Season, SeasonDTO> {
    List<SeasonDTO> getBySeriesId(int id);
}
