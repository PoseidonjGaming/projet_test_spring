package fr.perso.springserie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.ICategoryRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import fr.perso.springserie.task.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SeriesService extends BaseService<Series, SeriesDTO> implements ISeriesService {


    private final IFileService fileService;
    private final ISeasonRepo seasonRepo;
    private final ICategoryRepo categoryRepo;
    private final ObjectMapper objectMapper;

    @Autowired
    public SeriesService(ISeriesRepo repo, IFileService fileService, ISeasonRepo seasonRepo, ICategoryRepo categoryRepo, MapService mapService) {
        super(repo, SeriesDTO.class, Series.class, mapService);
        this.fileService = fileService;
        this.seasonRepo = seasonRepo;
        this.categoryRepo = categoryRepo;
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
            fileService.save(file,"series");
            seriesObj.setPoster(file.getOriginalFilename());
        }
        save(seriesObj);
    }

    @Override
    public List<SeriesDTO> getByCategoryIds(List<Integer> categoryIds) {
        return ((ISeriesRepo) repository).findByProjectCategoryIn(categoryIds).stream().map(this::toDTO).toList();
    }


    @Override
    public void saveWithSeasons(SeriesDTO seriesDTO) {
//        Series series=repository.save(toEntity(seriesDTO));
//        seriesDTO.getCategoryIds().forEach(number ->{
//            SeasonDTO seasonDTO=new SeasonDTO();
//            seasonDTO.setNumber(number);
//            seasonDTO.setSeriesId(series.getId());
//            seasonRepo.save(toEntity())
//        });
    }

}
