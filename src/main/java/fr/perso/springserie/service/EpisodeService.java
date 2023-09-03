package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.service.interfaces.IEpisodeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeService extends BaseService<Episode, EpisodeDTO> implements IEpisodeService {
    public EpisodeService(IEpisodeRepo repository) {
        super(repository, EpisodeDTO.class, Episode.class);
    }


    @Override
    public List<EpisodeDTO> getBySaisonIdIn(List<Integer> id) {
        return ((IEpisodeRepo) repository).findBySeasonIdIn(id).stream().map(this::toDTO).toList();
    }

    @Override
    public List<EpisodeDTO> search(String term) {
        return null;
    }
}
