package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.service.interfaces.crud.IEpisodeService;
import fr.perso.springserie.service.mapper.EpisodeMapper;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EpisodeService extends BaseService<Episode, EpisodeDTO> implements IEpisodeService {


    protected EpisodeService(IBaseRepo<Episode> repository,
                             EpisodeMapper mapper, MapService mapService) {
        super(repository, mapper, EpisodeDTO.class, Episode.class, mapService);
    }


    @Override
    public List<EpisodeDTO> getBySeasonIdIn(List<Integer> id) {
        return (id.get(0) == 0) ? null : ((IEpisodeRepo) repository).findBySeasonIdIn(id).stream().map(e ->
                mapper.convert(e, dtoClass)).toList();
    }

    @Override
    public Map<String, String> getTypes() {
        Map<String, String> types = super.getTypes();
        types.put("seriesId", "series");
        return types;
    }
}
