package fr.perso.springserie.service.imp;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.task.MapService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService implements IFileService {

    private final String root = System.getProperty("user.dir");
    private final MapService mapService;
    @Value("${app.storageFolder}")
    private String fileRoot;

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
    public ResponseEntity<?> load(String filename) {
        try (Stream<Path> files = Files.find(Paths.get(root, fileRoot), 3,
                (p, a) -> p.getFileName().toString().equals(filename))) {

            List<Path> fileList=files.toList();
            if(!fileList.isEmpty()){
                Path pathParent = fileList.get(0);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(Files.readAllBytes(pathParent));
            }else
                return ResponseEntity.notFound().build();

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
    public ResponseEntity<?> writeExcel(List<String> classList) {

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            classList.forEach(aClass -> {
                writeSheet(mapService.getClass(aClass), workbook, mapService.getService(aClass));
            });
            String filename = "data.xlsx";
            FileOutputStream outputStream = new FileOutputStream(Path.of(root, fileRoot, "files", filename).toFile());
            workbook.write(outputStream);

            return load(filename);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createFolder(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeSheet(Class<? extends BaseDTO> dtoClass, XSSFWorkbook workbook, IBaseService<BaseDTO> service) {

    }


}
