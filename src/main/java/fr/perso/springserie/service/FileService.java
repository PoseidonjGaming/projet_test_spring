package fr.perso.springserie.service;

import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IFileService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService implements IFileService {

    private final ISeriesRepo repo;

    @Value("${app.storageFolder}")
    private String fileRoot;

    public FileService(ISeriesRepo repo) {
        this.repo = repo;
    }

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

    @Override
    public InputStream writeExcel() {
        List<Series> list = repo.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Series");
            sheet.setColumnWidth(0, 6000);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


            XSSFFont font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);

            headerStyle.setFont(font);

            Row header = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            createHeaders(Series.class, header, headerStyle);
            list.forEach(series -> createCellData(Series.class, sheet.createRow(list.indexOf(series) + 1), style, series));
            System.out.println("test");

            createFolder(Path.of(System.getProperty("user.dir"), fileRoot, "files"));

            FileOutputStream outputStream = new FileOutputStream(Path.of(System.getProperty("user.dir"), fileRoot, "files", "test.xlsx").toFile());
            workbook.write(outputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream stream = load("test.xlsx");
        File file = Path.of(System.getProperty("user.dir"), fileRoot, "files", "test.xlsx").toFile();
        if(file.delete())
            System.out.println("delete");
        return stream;
    }

    private void createHeaders(@NotNull Class<Series> seriesClass, Row header, CellStyle headerStyle) {
        Field[] fields = seriesClass.getDeclaredFields();

        Arrays.stream(fields).forEach(field -> {
            Cell headerCell = header.createCell(header.getLastCellNum() + 1);
            headerCell.setCellValue(field.getName());
            headerCell.setCellStyle(headerStyle);
        });
    }

    private void createCellData(@NotNull Class<Series> seriesClass, Row row, CellStyle headerStyle, Series series) {
        Field[] fields = seriesClass.getDeclaredFields();

        Arrays.stream(fields).forEach(field -> {
            Cell headerCell = row.createCell(row.getLastCellNum() + 1);
            field.setAccessible(true);
            try {
                headerCell.setCellValue(field.get(series).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(false);
            headerCell.setCellStyle(headerStyle);
        });
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
}
