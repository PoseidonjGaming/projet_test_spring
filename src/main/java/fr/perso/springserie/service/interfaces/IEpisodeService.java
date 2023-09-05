package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;

import java.util.List;

public interface IEpisodeService extends IBaseService<Episode, EpisodeDTO>{
    List<EpisodeDTO> getBySeasonIdIn(List<Integer> id);
}
