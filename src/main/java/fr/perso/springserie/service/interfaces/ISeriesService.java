package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.SeriesDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISeriesService extends IBaseService<SeriesDTO> {
    void saveWithFile(MultipartFile file, String series);

    List<SeriesDTO> getByCategoryIds(List<Integer> categoryIds);

    List<SeriesDTO> search(String term, List<Integer> categoryIds);
}
