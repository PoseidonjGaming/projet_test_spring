package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.crud.IEpisodeService;
import fr.perso.springserie.service.mapper.EpisodeMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeService extends BaseService<Episode, EpisodeDTO> implements IEpisodeService {


    private final ISeasonRepo seasonRepo;
    private final ISeriesRepo seriesRepo;

    protected EpisodeService(IBaseRepo<Episode> repository,
                             ISeasonRepo seasonRepo, ISeriesRepo seriesRepo,
                             EpisodeMapper mapper, MapService mapService) {
        super(repository, mapper, EpisodeDTO.class, Episode.class, mapService);
        this.seasonRepo = seasonRepo;
        this.seriesRepo = seriesRepo;
    }


    @Override
    public List<EpisodeDTO> getBySeasonIdIn(List<Integer> id) {
        return (id.get(0) == 0) ? null : ((IEpisodeRepo) repository).findBySeasonIdIn(id).stream().map(e ->
                mapper.convert(e, dtoClass)).toList();
    }

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
