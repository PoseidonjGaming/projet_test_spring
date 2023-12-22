package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.PagedResponse;
import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;
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
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

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
    protected Predicate<EpisodeDTO> predicate(SearchDTO<EpisodeDTO> searchDTO) {
        return episodeDTO -> {
            if (searchDTO.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
                return isBetween(episodeDTO.getReleaseDate(), searchDTO.getStartDate(), searchDTO.getEndDate())
                        && equalsId(episodeDTO.getSeriesId(), searchDTO.getDto().getSeriesId()) ;
            }else {
                return isBetween(episodeDTO.getReleaseDate(),searchDTO.getStartDate(),searchDTO.getEndDate()) ||
                        equalsId(episodeDTO.getSeriesId(), searchDTO.getDto().getSeriesId());
            }
        };
    }


    @Override
    public List<EpisodeDTO> getBySeasonIdIn(List<Integer> id) {
        return (id.get(0) == 0) ? null : ((IEpisodeRepo) repository).findBySeasonIdIn(id).stream().map(e ->
                mapper.convert(e, dtoClass)).toList();
    }

    @Override
    public List<EpisodeDTO> search(SearchDTO<EpisodeDTO> searchDTO) {
        return super.search(searchDTO).stream().filter(predicate(searchDTO)).toList();
    }



    @Override
    public PagedResponse<EpisodeDTO> search(SearchDTO<EpisodeDTO> searchDto, int size, int page) {
        PagedResponse<EpisodeDTO> search = super.search(searchDto, size, page);
        search.setContent(search.getContent().stream().filter(predicate(searchDto)).toList());
        return search;
    }

    @Override
    public List<EpisodeDTO> sortSearch(SearchDTO<EpisodeDTO> searchDto, SortDTO sortDTO) {
        List<EpisodeDTO> episodeDTOS = super.sortSearch(searchDto, sortDTO);
        return episodeDTOS.stream().filter(predicate(searchDto)).toList();
    }

    @Override
    public PagedResponse<EpisodeDTO> sortSearch(SearchDTO<EpisodeDTO> searchDto, SortDTO sortDTO, int size, int pageNumber) {
        PagedResponse<EpisodeDTO> episodeDTOPagedResponse = super.sortSearch(searchDto, sortDTO, size, pageNumber);
        episodeDTOPagedResponse.setContent(episodeDTOPagedResponse.getContent().stream().filter(predicate(searchDto)).toList());
        return episodeDTOPagedResponse;
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
