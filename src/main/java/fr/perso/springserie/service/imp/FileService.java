package fr.perso.springserie.service.imp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.perso.springserie.interceptor.exception.FieldException;
import fr.perso.springserie.interceptor.exception.FileException;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.task.MapService;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.perso.springserie.service.utility.FileUtility.*;
import static fr.perso.springserie.service.utility.ServiceUtility.*;

@Service
public class FileService implements IFileService {

    private final String root = System.getProperty("user.dir");
    private final MapService mapService;
    @Value("${app.storageFolder}")
    private String fileRoot;
    @Value("${app.resourceFolder}")
    private String resourcePath;

    @Lazy
    public FileService(MapService mapService) {
        this.mapService = mapService;
    }


    @Override
    public void save(MultipartFile file, String type) {
        Path path = Path.of(root, fileRoot, type, file.getOriginalFilename());
        createFolder(path);
        try {
            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<byte[]> load(String filename) {
        try (Stream<Path> files = Files.find(Paths.get(root, resourcePath), 3,
                (p, a) -> p.getFileName().toString().equals(filename))) {


            AtomicReference<ResponseEntity<byte[]>> response = new AtomicReference<>();

            files.findFirst().ifPresentOrElse(file -> {
                try {
                    response.set(ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(Files.readAllBytes(file)));
                } catch (IOException e) {
                    throw new FileException(e);
                }
            }, () -> response.set(ResponseEntity.notFound().build()));

            return response.get();
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public void saves(List<MultipartFile> files, String type) {
        if (files != null)
            files.forEach(multipartFile -> save(multipartFile, type));
    }

    @Override
    public ResponseEntity<byte[]> writeExcel(@NotNull List<String> classList) {
        return null;
    }

    @Override
    public void readExcel(@NotNull MultipartFile file) {

    }


}
