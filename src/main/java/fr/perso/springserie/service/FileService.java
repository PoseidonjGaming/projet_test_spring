package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.*;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.ISeasonService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService implements IFileService {

    @Autowired
    @Lazy
    private ISeriesService seriesService;

    @Autowired
    private ISeasonService seasonService;

    @Value("${app.storageFolder}")
    private String fileRoot;


    @Override
    public void save(MultipartFile file) {
        Path path = Path.of(System.getProperty("user.dir"), fileRoot, file.getOriginalFilename());
        createFolder(path);
        try {
            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> load(String filename) {
        try (Stream<Path> files = Files.find(Paths.get(System.getProperty("user.dir"), fileRoot), 4,
                (p, a) -> p.getFileName().toString().equals(filename))) {

            Path pathParent = files.toList().get(0);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(Files.readAllBytes(pathParent));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public void saves(List<MultipartFile> files) {
        if (files != null)
            files.forEach(this::save);
    }

    @Override
    public ResponseEntity<?> writeExcel(List<Boolean> booleanList) {

        if (booleanList.size() == 5) {
            List<Class<? extends BaseDTO>> classList = new ArrayList<>();

            for (int i = 0; i < booleanList.size(); i++) {
                switch (i) {
                    case 0 -> classList.add(SeriesDTO.class);
                    case 1 -> classList.add(SeasonDTO.class);
                    case 2 -> classList.add(ActorDTO.class);
                    case 3 -> classList.add(CharacterDTO.class);
                    case 4 -> classList.add(EpisodeDTO.class);
                    default -> System.out.println("Class not found");
                }
            }


            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                classList.forEach(aClass -> {
                    System.out.println(aClass.getSimpleName());
                    switch (aClass.getSimpleName()) {
                        case "SeriesDTO" -> write(aClass, workbook, seriesService);
                        case "SeasonDTO" -> write(aClass, workbook, seasonService);
                        default -> System.out.println("Class not found");
                    }
                });
                createFolder(Path.of(System.getProperty("user.dir"), fileRoot, "files"));
                String filename = "data.xlsx";
                FileOutputStream outputStream = new FileOutputStream(Path.of(System.getProperty("user.dir"), fileRoot, "files", filename).toFile());
                workbook.write(outputStream);

                return load(filename);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return null;

    }

    private void createHeaders(@NotNull Class<? extends BaseDTO> classe, Row header, CellStyle headerStyle) {
        Field[] fields = classe.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            Cell headerCell = header.createCell((header.getLastCellNum() == -1) ? 0 : header.getLastCellNum());
            headerCell.setCellValue(field.getName());
            headerCell.setCellStyle(headerStyle);
        });
    }

    private <D extends BaseDTO> void createCellData(@NotNull Class<? extends BaseDTO> seriesClass, Row
            row, CellStyle headerStyle, D dto) {
        Field[] fields = seriesClass.getDeclaredFields();

        Arrays.stream(fields).forEach(field -> {
            Cell headerCell = row.createCell((row.getLastCellNum() == -1) ? 0 : row.getLastCellNum());
            if (field.getName().equals("summary"))
                headerStyle.setWrapText(true);
            field.setAccessible(true);
            try {
                headerCell.setCellValue(field.get(dto).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(false);
            headerCell.setCellStyle(headerStyle);
        });
    }

    private Sheet createSheet(Workbook workbook, Class<?> aClass) {
        return workbook.createSheet(aClass.getSimpleName().replace("DTO", ""));
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

    private <D extends BaseDTO> void write(Class<? extends BaseDTO> dtoClass, Workbook
            workbook, IBaseService<D> service) {
        List<D> list;
        Sheet sheet = createSheet(workbook, dtoClass);
        for (int i = 0; i < dtoClass.getDeclaredFields().length; i++) {
            sheet.setColumnWidth(i, 6000);
        }

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headers = sheet.createRow(0);
        createHeaders(dtoClass, headers, headerStyle);
        list = service.getAll();


        if (list != null) {
            CellStyle style = workbook.createCellStyle();
            System.out.println(sheet.getLastRowNum());
            list.forEach(dto -> createCellData(dtoClass, sheet.createRow(
                    (sheet.getLastRowNum() == -1) ? 0 : sheet.getLastRowNum()), style, dto));
        }
    }
}
