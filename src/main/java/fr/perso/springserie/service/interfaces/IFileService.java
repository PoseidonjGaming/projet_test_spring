package fr.perso.springserie.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public interface IFileService {
    void save(MultipartFile file);

    InputStream load(String filename);

    void saves(List<MultipartFile> files);

    InputStream writeExcel(List<Boolean> booleanList);
}
