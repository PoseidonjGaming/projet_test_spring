package fr.perso.springserie.service.imp.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.perso.springserie.interceptor.exception.GenericException;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.crud.ISeriesService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SeriesService extends BaseService<Series, SeriesDTO> implements ISeriesService {

    private final IFileService fileService;
    private final ObjectMapper objectMapper;

    public SeriesService(ISeriesRepo repository, IMapper mapper, MapService mapService,
                         IFileService fileService, ObjectMapper objectMapper) {
        super(repository, mapper, SeriesDTO.class, Series.class, mapService);
        this.fileService = fileService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void saveWithFile(MultipartFile file, String series) {
        SeriesDTO seriesObj;
        try {
            seriesObj = objectMapper.readValue(series, SeriesDTO.class);
        } catch (JsonProcessingException e) {
            throw new GenericException(e);
        }
        if (file != null && !file.isEmpty()) {
            fileService.save(file, "series");
            seriesObj.setPoster(file.getOriginalFilename());
        }
        save(seriesObj);
    }
}
