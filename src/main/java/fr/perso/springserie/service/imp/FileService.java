package fr.perso.springserie.service.imp;

import fr.perso.springserie.interceptor.exception.FileException;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.task.MapService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static fr.perso.springserie.service.utility.FileUtility.createFolder;

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
