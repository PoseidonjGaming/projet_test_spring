package fr.perso.springserie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.dto.special.SearchDateDto;
import fr.perso.springserie.model.dto.special.SearchDto;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeriesService extends BaseService<Series, SeriesDTO> implements ISeriesService {


    private final IFileService fileService;
    private final ISeasonRepo seasonRepo;
    private final ObjectMapper objectMapper;

    @Autowired
    public SeriesService(ISeriesRepo repo, IFileService fileService, ISeasonRepo seasonRepo, MapService mapService, IMapper customMapper) {
        super(repo, SeriesDTO.class, Series.class, mapService, customMapper);
        this.fileService = fileService;
        this.seasonRepo = seasonRepo;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
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

    @Override
    public List<SeriesDTO> getByCategoryIds(List<Integer> categoryIds) {
        return ((ISeriesRepo) repository).findByProjectCategoryIn(categoryIds).stream().map(e -> customMapper.convert(e, dtoClass)).toList();
    }

    @Override
    public List<SeriesDTO> search(SeriesDTO dto, SearchDto searchDto, SearchDateDto searchDateDto) {
        return super.search(dto, searchDto, searchDateDto).stream().filter(seriesDTO->isBetween(searchDateDto, seriesDTO.getReleaseDate())).toList();
    }

    @Override
    public SeriesDTO savesWithSeasons(SeriesDTO dto, int seasons) {
        SeriesDTO series=save(dto);
        List<SeasonDTO> seasonsList=new ArrayList<>();
        for (int i = 0; i < seasons; i++) {
            int number = i+1;
            seasonsList.add(new SeasonDTO(series.getId(), number, new ArrayList<>()));
        }
        seasonRepo.saveAll(customMapper.convertList(seasonsList, Season.class));
        return series;
    }


}
