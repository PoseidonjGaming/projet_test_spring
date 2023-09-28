package fr.perso.springserie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.ICategoryRepo;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.ISeriesService;
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
    public SeriesService(ISeriesRepo repo, IFileService fileService, ISeasonRepo seasonRepo, ICategoryRepo categoryRepo) {
        super(repo, SeriesDTO.class, Series.class);
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
            fileService.save(file);
            seriesObj.setPoster(file.getOriginalFilename());
        }
        save(seriesObj);
    }

    @Override
    public List<SeriesDTO> getByCategoryIds(List<Integer> categoryIds) {
        return ((ISeriesRepo) repository).findByCategoryIn(categoryIds).stream().map(this::toDTO).toList();
    }

    @Override
    public List<SeriesDTO> search(String term, List<Integer> categoryIds) {
        return ((ISeriesRepo) repository).findByNameContainingAndCategoryIn(term, categoryIds).stream().map(this::toDTO).toList();
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

    @Override
    public List<SeriesDTO> search(String term) {
        return ((ISeriesRepo) repository).findByNameContaining(term).stream().map(this::toDTO).toList();
    }

    @Override
    public Series toEntity(SeriesDTO dto) {
        Series entity = super.toEntity(dto);
        entity.setCategory(getRelatedEntities(dto.getCategoryIds(), categoryRepo));
        entity.setSeasons(getRelatedEntities(dto.getSeasonsIds(), seasonRepo));
        return entity;
    }
}
