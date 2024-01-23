package fr.perso.springserie.service.mapper;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.task.MapService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EpisodeMapper extends Mapper implements IMapper {
    public EpisodeMapper(MapService mapService) {
        super(mapService);
    }

    @Override
    public <S, T> T convert(S source, Class<T> targetClass) {
        T convert = super.convert(source, targetClass);
        if (source instanceof Episode episode && convert instanceof EpisodeDTO episodeDTO) {
            episodeDTO.setSeriesId(episode.getSeason().getSeries().getId());
        }
        return convert;
    }
}
