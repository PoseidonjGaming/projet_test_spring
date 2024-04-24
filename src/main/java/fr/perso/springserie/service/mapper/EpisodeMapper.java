package fr.perso.springserie.service.mapper;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

@Service
public class EpisodeMapper extends Mapper{
    public EpisodeMapper(MapService mapService) {
        super(mapService);
    }

    @Override
    public <S, T> T convert(S source, Class<T> targetClass) {
        T target=super.convert(source, targetClass);

        if(target instanceof EpisodeDTO episodeDTO && source instanceof Episode episode){
            episodeDTO.setSeriesId(episode.getSeason().getSeries().getId());
        }

        return target;
    }
}
