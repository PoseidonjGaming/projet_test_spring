package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IEpisodeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EpisodeService extends BaseService<Episode, EpisodeDTO> implements IEpisodeService {

    private final ISeasonRepo seasonRepo;
    private final ISeriesRepo seriesRepo;

    public EpisodeService(IEpisodeRepo repository, ISeasonRepo seasonRepo, ISeriesRepo seriesRepo) {
        super(repository, EpisodeDTO.class, Episode.class);
        this.seasonRepo = seasonRepo;
        this.seriesRepo = seriesRepo;
    }


    @Override
    public List<EpisodeDTO> getBySeasonIdIn(List<Integer> id) {
        return (id.get(0) == 0) ? null : ((IEpisodeRepo) repository).findBySeasonIdIn(id).stream().map(this::toDTO).toList();
    }

    @Override
    public List<EpisodeDTO> search(String term) {
        return null;
    }

    @Override
    public EpisodeDTO toDTO(Episode entity) {
        EpisodeDTO dto = super.toDTO(entity);
        dto.setSeriesId(entity.getSeason().getSeries().getId());
        return dto;
    }

    @Override
    public void delete(int id) {
        EpisodeDTO dto = getById(id);
        super.delete(id);
        seasonRepo.findById(dto.getSeasonId()).ifPresent(season -> {
            if (season.getEpisodes().isEmpty())
                seasonRepo.delete(season);
        });
    }

    @Override
    public void save(EpisodeDTO episodeDTO) {
        seasonRepo.findById(episodeDTO.getSeasonId()).or(() -> {
            Season season = new Season();
            Optional<Series> series = seriesRepo.findById(episodeDTO.getSeriesId());
            series.ifPresent(series1 -> {
                season.setSeries(series1);
                season.setNumber(series1.getSeasons().size() + 1);
            });
            return Optional.of(seasonRepo.save(season));
        }).ifPresent(season -> episodeDTO.setSeasonId(season.getId()));

        super.save(episodeDTO);
    }
}
