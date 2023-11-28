package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.SeasonDTO;

import java.util.List;

public interface ISeasonService extends IBaseService<SeasonDTO> {
    List<SeasonDTO> getBySeriesId(int id);
}
