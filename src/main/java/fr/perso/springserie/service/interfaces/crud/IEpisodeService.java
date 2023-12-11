package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.EpisodeDTO;

import java.util.List;

public interface IEpisodeService extends IBaseService<EpisodeDTO>{
    List<EpisodeDTO> getBySeasonIdIn(List<Integer> id);
}
