package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IEpisodeService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeService extends BaseService<Episode, EpisodeDTO> implements IEpisodeService {

    private final ISeasonRepo seasonRepo;
    private final ISeriesRepo seriesRepo;

    public EpisodeService(IEpisodeRepo repository, ISeasonRepo seasonRepo, ISeriesRepo seriesRepo,
                          MapService mapService, IMapper customMapper) {
        super(repository, EpisodeDTO.class, Episode.class, mapService, customMapper);
        this.seasonRepo = seasonRepo;
        this.seriesRepo = seriesRepo;
    }


    @Override
    public List<EpisodeDTO> getBySeasonIdIn(List<Integer> id) {
        return (id.get(0) == 0) ? null : ((IEpisodeRepo) repository).findBySeasonIdIn(id).stream().map(e->customMapper.convert(e, dtoClass)).toList();
    }


//    @Override
//    public EpisodeDTO toDTO(Episode entity) {
//        EpisodeDTO dto = super.toDTO(entity);
//        dto.setSeriesId(entity.getSeason().getSeries().getId());
//        return dto;
//    }

    @Override
    public void delete(int id) {
        EpisodeDTO dto = getById(id);
        super.delete(id);
        seasonRepo.findById(dto.getSeasonId()).ifPresent(season -> {
            if (season.getEpisode().isEmpty())
                seasonRepo.delete(season);
        });
    }

    @Override
    public EpisodeDTO save(EpisodeDTO episodeDTO) {
        Season season = seasonRepo.findById(episodeDTO.getSeasonId()).orElse(new Season());
        if (season.getId() == 0) {
            Series series = seriesRepo.findById(episodeDTO.getSeriesId()).orElse(null);
            if (series != null) {
                season.setSeries(series);
                int number = series.getSeason().size() + 1;
                season.setNumber(number);
            }

        }
        episodeDTO.setSeasonId(seasonRepo.save(season).getId());
        return super.save(episodeDTO);
    }
}
