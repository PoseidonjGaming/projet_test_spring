package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;

public interface IFileService {
    void save(MultipartFile file);

    InputStream load(String filename);

    void saves(List<MultipartFile> files);

    InputStream write(String filename);

    <E extends BaseEntity, D extends BaseDTO> void load(BiConsumer<IBaseService<E, SeriesDTO>, SeriesDTO> biConsumer, IBaseService<E, SeriesDTO> service);
}
