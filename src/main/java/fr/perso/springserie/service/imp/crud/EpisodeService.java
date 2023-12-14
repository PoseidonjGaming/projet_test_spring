package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.PagedResponse;
import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.crud.IEpisodeService;
import fr.perso.springserie.service.mapper.EpisodeMapper;
import fr.perso.springserie.service.mapper.IMapper;
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
    public List<EpisodeDTO> search(SearchDTO<EpisodeDTO> searchDTO) {

        return super.search(searchDTO).stream().filter(episodeDTO ->
                        isBetween(episodeDTO.getReleaseDate(), searchDTO.getStartDate(), searchDTO.getEndDate()))
                .filter(episodeDTO -> {
                    if (searchDTO.getDto().getSeriesId() > 0) {
                        return episodeDTO.getSeriesId() == searchDTO.getDto().getSeriesId();
                    }
                    return true;
                }).toList();
    }

    @Override
    public PagedResponse<EpisodeDTO> search(SearchDTO<EpisodeDTO> searchDto, int size, int page) {
        PagedResponse<EpisodeDTO> search = super.search(searchDto, size, page);
        search.setContent(search.getContent().stream().filter(episodeDTO ->
                        isBetween(episodeDTO.getReleaseDate(), searchDto.getStartDate(), searchDto.getEndDate()))
                .filter(episodeDTO -> {
                    if (searchDto.getDto().getSeriesId() > 0) {
                        return episodeDTO.getSeriesId() == searchDto.getDto().getSeriesId();
                    }
                    return true;
                }).toList());
        return search;
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