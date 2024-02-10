package fr.perso.springserie.service.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    void save(MultipartFile file, String type);

    ResponseEntity<byte[]> load(String filename);

    void saves(List<MultipartFile> files, String type);

    ResponseEntity<byte[]> writeExcel(List<String> booleanList);

    void readExcel(MultipartFile file);
}
