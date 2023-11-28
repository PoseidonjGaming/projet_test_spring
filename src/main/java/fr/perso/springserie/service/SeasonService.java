package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.ISeasonService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService extends BaseService<Season, SeasonDTO> implements ISeasonService {

    private final ISeriesRepo seriesRepo;
    private final IEpisodeRepo episodeRepo;

    @Autowired
    public SeasonService(ISeasonRepo repository, ISeriesRepo seriesRepo, IEpisodeRepo episodeRepo,
                         MapService mapService, IMapper customMapper) {
        super(repository, SeasonDTO.class, Season.class, mapService, customMapper);
        this.seriesRepo = seriesRepo;
        this.episodeRepo = episodeRepo;
    }


    @Override
    public List<SeasonDTO> getBySeriesId(int id) {
        return ((ISeasonRepo) repository).findBySeriesId(id).stream().map(e->customMapper.convert(e, dtoClass)).toList();
    }
}
