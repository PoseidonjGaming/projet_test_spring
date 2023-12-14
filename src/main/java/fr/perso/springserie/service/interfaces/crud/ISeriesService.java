package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.SeriesDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ISeriesService extends IBaseService<SeriesDTO> {
    void saveWithFile(MultipartFile file, String series);

}
