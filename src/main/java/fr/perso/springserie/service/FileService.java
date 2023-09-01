package fr.perso.springserie.service;

import fr.perso.springserie.service.interfaces.IFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService implements IFileService {

    @Value("${app.storageFolder}")
    private String fileRoot;

    @Override
    public void save(MultipartFile file) {
        Path path = Path.of(System.getProperty("user.dir"), fileRoot, file.getOriginalFilename());
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {

            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream load(String filename) {
        try (Stream<Path> files = Files.find(Paths.get(System.getProperty("user.dir"), fileRoot), 3,
                (p, a) -> p.getFileName().toString().equals(filename))) {

            Path pathParent = files.toList().get(0);

            String fileResourcePath = pathParent.getParent().toString().split("resources")[1];

            return getClass().getResourceAsStream(String.join("\\", fileResourcePath, filename).replace("\\", "/"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saves(List<MultipartFile> files) {
        if (files != null)
            files.forEach(this::save);
    }
}
