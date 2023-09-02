package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.ISeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService extends BaseService<Season, SeasonDTO> implements ISeasonService {

    private final ISeriesRepo seriesRepo;
    private final IEpisodeRepo episodeRepo;

    @Autowired
    public SeasonService(ISeasonRepo repository, ISeriesRepo seriesRepo, IEpisodeRepo episodeRepo) {
        super(repository, SeasonDTO.class, Season.class);
        this.seriesRepo = seriesRepo;
        this.episodeRepo = episodeRepo;
    }

    @Override
    public Season toEntity(SeasonDTO dto) {
        Season season = super.toEntity(dto);
        if (dto.getSeriesId() != 0)
            season.setSeries(seriesRepo.findById(dto.getSeriesId()).orElse(null));
        return season;
    }

    @Override
    public SeasonDTO toDTO(Season entity) {
        SeasonDTO dto = super.toDTO(entity);
        dto.setEpisodesIds(entity.getEpisodes().stream().map(BaseEntity::getId).toList());
        return dto;
    }

    @Override
    public List<SeasonDTO> getBySeriesId(int id) {
        return ((ISeasonRepo) repository).findBySeriesId(id).stream().map(this::toDTO).toList();
    }
}
