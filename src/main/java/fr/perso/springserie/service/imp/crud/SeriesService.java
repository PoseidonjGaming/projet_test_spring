package fr.perso.springserie.service.imp.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.perso.springserie.model.PagedResponse;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.crud.ISeriesService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Predicate;

@Service
public class SeriesService extends BaseService<Series, SeriesDTO> implements ISeriesService {

    private final IFileService fileService;
    private final IBaseRepo<Season> seasonRepo;
    private final ObjectMapper objectMapper;

    public SeriesService(ISeriesRepo repository, IMapper mapper, MapService mapService,
                         IFileService fileService, IBaseRepo<Season> seasonRepo,
                         ObjectMapper objectMapper) {
        super(repository, mapper, SeriesDTO.class, Series.class, mapService);
        this.fileService = fileService;
        this.seasonRepo = seasonRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SeriesDTO> search(SearchDTO<SeriesDTO> searchDto) {
        return super.search(searchDto).stream().filter(predicate(searchDto)).toList();
    }

    @Override
    protected Predicate<SeriesDTO> predicate(SearchDTO<SeriesDTO> searchDTO) {
        return seriesDTO -> {
            if (searchDTO.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
                return isBetween(seriesDTO.getReleaseDate(), searchDTO.getStartDate(), searchDTO.getEndDate())
                        && filterList(seriesDTO.getCategoryIds(), searchDTO.getDto().getCategoryIds());
            } else {
                return isBetween(seriesDTO.getReleaseDate(), searchDTO.getStartDate(), searchDTO.getEndDate()) ||
                        filterList(seriesDTO.getCategoryIds(), searchDTO.getDto().getCategoryIds());
            }
        };
    }

    @Override
    public PagedResponse<SeriesDTO> search(SearchDTO<SeriesDTO> searchDto, int size, int page) {
        PagedResponse<SeriesDTO> search = super.search(searchDto, size, page);
        search.setContent(search.getContent().stream().filter(seriesDTO -> isBetween(seriesDTO.getReleaseDate(), searchDto.getStartDate(), searchDto.getEndDate())).toList());
        return search;
    }

    @Override
    public PagedResponse<SeriesDTO> sortSearch(SearchDTO<SeriesDTO> searchDto, SortDTO sortDTO, int size, int pageNumber) {
        PagedResponse<SeriesDTO> search = super.search(searchDto, size, pageNumber);
        search.setContent(search.getContent().stream().filter(seriesDTO -> isBetween(seriesDTO.getReleaseDate(), searchDto.getStartDate(), searchDto.getEndDate())).toList());
        return search;
    }

    @Override
    public List<SeriesDTO> sortSearch(SearchDTO<SeriesDTO> searchDto, SortDTO sortDTO) {
        return super.search(searchDto).stream().filter(seriesDTO ->
                isBetween(seriesDTO.getReleaseDate(), searchDto.getStartDate(), searchDto.getEndDate())).toList();
    }

    @Override
    public void saveWithFile(MultipartFile file, String series) {
        SeriesDTO seriesObj;
        try {
            seriesObj = objectMapper.readValue(series, SeriesDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (file != null && !file.isEmpty()) {
            fileService.save(file, "series");
            seriesObj.setPoster(file.getOriginalFilename());
        }
        save(seriesObj);
    }
}
