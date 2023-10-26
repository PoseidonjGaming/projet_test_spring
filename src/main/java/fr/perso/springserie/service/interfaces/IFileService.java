package fr.perso.springserie.service.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    void save(MultipartFile file, String type);

    ResponseEntity<?> load(String filename);

    void saves(List<MultipartFile> files, String type);

    ResponseEntity<?> writeExcel(List<Boolean> booleanList);
}
