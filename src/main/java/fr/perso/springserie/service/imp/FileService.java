package fr.perso.springserie.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.task.MapService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static fr.perso.springserie.service.utility.FileUtility.*;
import static fr.perso.springserie.service.utility.ServiceUtility.set;

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
        try (Stream<Path> files = Files.find(Paths.get(root, fileRoot), 3,
                (p, a) -> p.getFileName().toString().equals(filename))) {

            List<Path> fileList = files.toList();
            if (!fileList.isEmpty()) {
                Path pathParent = fileList.get(0);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(Files.readAllBytes(pathParent));
            } else
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
    public ResponseEntity<Resource> writeExcel(@NotNull List<String> classList) {

        try (Workbook workbook = new XSSFWorkbook()) {
            classList.forEach(clazzString -> {
                IBaseService<BaseDTO> service = mapService.getService(clazzString);
                if (service != null) {
                    List<BaseDTO> list = service.getAll();

                    Sheet sheet = workbook.createSheet(clazzString);
                    sheet.setColumnWidth(0, 6000);

                    Row headers = sheet.createRow(0);
                    CellStyle headerStyle = createHeaderStyle(workbook);

                    createHeader(headers, headerStyle, mapService.getClass(clazzString));

                    CellStyle style = workbook.createCellStyle();
                    style.setWrapText(true);
                    style.setAlignment(HorizontalAlignment.FILL);

                    list.forEach(baseDTO -> {
                        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                        createRowValue(baseDTO, row);
                    });
                }
            });
            if (workbook.sheetIterator().hasNext()) {
                writeToFile(workbook, root, resourcePath);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Stream<Path> files = Files.find(Paths.get(root), 4,
                (p, a) -> p.getFileName().toString().equals("data.xlsx"))) {

            List<Path> fileList = files.toList();
            if (!fileList.isEmpty()) {
                Path pathParent = fileList.get(0);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(pathParent.toFile().length())
                        .body(new ByteArrayResource(Files.readAllBytes(pathParent)));
            } else
                return ResponseEntity.notFound().build();

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public void readExcel(@NotNull MultipartFile file) {
        Path path = Path.of(root, "files", file.getOriginalFilename());
        createFolder(path);
        try {
            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Workbook workbook = new XSSFWorkbook(new File(path.toUri()))) {
            workbook.forEach(sheet -> {
                List<Field> fields = new ArrayList<>();
                Class<? extends BaseDTO> baseClass = mapService.getClass(sheet.getSheetName());
                IBaseService<BaseDTO> service = mapService.getService(sheet.getSheetName());
                sheet.forEach(row -> {
                    if (row.getRowNum() == 0) {
                        getFields(row, fields, baseClass);
                    } else {
                        try {
                            BaseDTO dto = baseClass.getDeclaredConstructor().newInstance();
                            getValues(row, dto, fields);
                            System.out.println(dto);

                            List<BaseDTO> searchedDTOS = service.search(new SearchDTO<>(dto,
                                    ExampleMatcher.MatchMode.ALL, ExampleMatcher.StringMatcher.DEFAULT, null, null));
                            if (searchedDTOS.isEmpty()) {
                                service.save(dto);
                            }
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                 NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }


                    }
                });
            });
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private <D extends BaseDTO> void getValues(Row row, D dto, List<Field> fields) {
        row.forEach(cell -> {
            Field targetField = fields.get(cell.getColumnIndex());
            Object value;
            if (targetField.getType().equals(LocalDate.class)) {

                value = LocalDate.parse(cell.getStringCellValue());
            } else if (targetField.getType().equals(Integer.class)) {
                value = (cell.getStringCellValue().equals("null")) ? null : cell.getNumericCellValue();
            } else if (cell.getStringCellValue().startsWith("[")
                    && cell.getStringCellValue().endsWith("]") && targetField.getType().equals(List.class)) {
                try {
                    value = new ObjectMapper().readValue(cell.getStringCellValue(), List.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else if (targetField.getType().isEnum()) {
                value = Arrays.stream(targetField.getType().getEnumConstants()).filter(e -> e.equals(cell.getStringCellValue())).findFirst().get();
            } else {
                value = (cell.getStringCellValue().equals("null"))? null: cell.getStringCellValue();
            }

            set(value, dto, targetField);
        });

    }

    private void getFields(Row row, List<Field> fields, Class<? extends BaseDTO> baseClass) {
        row.forEach(cell -> {
            try {

                fields.add(baseClass.getDeclaredField(cell.getStringCellValue()));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
