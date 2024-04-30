package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.mapper.IMapper;
import org.springframework.stereotype.Service;

@Service
public class EpisodeService extends BaseService<Episode, EpisodeDTO> {
    protected EpisodeService(IBaseRepository<Episode> repository, IMapper mapper) {
        super(repository, mapper, EpisodeDTO.class, Episode.class);
    }
}
