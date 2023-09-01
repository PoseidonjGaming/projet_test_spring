package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.SaisonDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.model.entity.Saison;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.repository.ISaisonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.ISaisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaisonService extends BaseService<Saison, SaisonDTO> implements ISaisonService {

    private final ISeriesRepo seriesRepo;
    private final IEpisodeRepo episodeRepo;

    @Autowired
    public SaisonService(ISaisonRepo repository, ISeriesRepo seriesRepo, IEpisodeRepo episodeRepo) {
        super(repository, SaisonDTO.class, Saison.class);
        this.seriesRepo = seriesRepo;
        this.episodeRepo = episodeRepo;

        mapper.typeMap(SaisonDTO.class, Saison.class).addMapping(SaisonDTO::getSeriesId, (saison, o) -> saison.setSerie(null));
    }

    @Override
    public Saison toEntity(SaisonDTO dto) {
        Saison saison = super.toEntity(dto);
        if (dto.getSeriesId() != 0)
            saison.setSerie(seriesRepo.findById(dto.getSeriesId()).orElse(null));
        return saison;
    }

    @Override
    public SaisonDTO toDTO(Saison entity) {
        SaisonDTO dto=super.toDTO(entity);
        dto.setSeriesId(entity.getSerie().getId());
        dto.setEpisodesIds(entity.getEpisodes().stream().map(BaseEntity::getId).toList());
        return dto;
    }

    @Override
    public List<SaisonDTO> getBySeriesId(int id) {
        return ((ISaisonRepo) repository).findBySerieId(id).stream().map(this::toDTO).toList();
    }
}
