package fr.perso.springserie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.ISeasonService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SeriesService extends BaseService<Series, SeriesDTO> implements ISeriesService {


    private final IFileService fileService;
    private final ISeasonService saisonService;

    @Autowired
    public SeriesService(ISeriesRepo repo, IFileService fileService, ISeasonService saisonService) {
        super(repo, SeriesDTO.class, Series.class);
        this.fileService = fileService;
        this.saisonService = saisonService;
    }

    @Override
    public void saveWithFile(MultipartFile file, String series) {
        ObjectMapper objectMapper = new ObjectMapper();
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
    public SeriesDTO toDTO(Series entity) {
        SeriesDTO dto = super.toDTO(entity);
        dto.setSeasonsIds(map(entity.getSeasons()));
        return dto;
    }
}
