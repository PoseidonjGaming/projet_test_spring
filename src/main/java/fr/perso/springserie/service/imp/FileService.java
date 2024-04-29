package fr.perso.springserie.service.imp;

import fr.perso.springserie.interceptor.exception.GenericException;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.MapService;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
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
            throw new GenericException(e);
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
                    throw new GenericException(e);
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
//        try (OutputStream stream = Files.newOutputStream(Paths.get(root, resourcePath, "datas.xlsx"))) {
//            Workbook workbook = new Workbook(stream, "SpringSeries", "1.0");
//            Worksheet sheet = workbook.newWorksheet("series");
//            sheet.width(0, 25);
//
//            sheet.range(0, 0, 2, 1).style().fontName("Arial").wrapText(true).set();
//            sheet.value(0, 0, "Id");
//
//            List<? extends BaseDTO> seriesDTOS = mapService.getService("series").getAll();
//
//            for (int i = 0; i < seriesDTOS.size(); i++) {
//                sheet.value(i + 1, 0, seriesDTOS.get(i).getId());
//            }
//            workbook.finish();
//
//        } catch (IOException e) {
//            throw new GenericException(e);
//        }

        return null;
    }

    @Override
    public void readExcel(@NotNull MultipartFile file) {

    }


}
