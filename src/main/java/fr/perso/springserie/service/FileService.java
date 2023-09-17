package fr.perso.springserie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.interfaces.IFileService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class FileService implements IFileService {

    private final ObjectMapper mapper;
    private final ISeriesRepo seriesRepo;

    private final String property = System.getProperty("user.dir");

    @Value("${app.storageFolder}")
    private String fileRoot;

    public FileService(ISeriesRepo repo) {
        this.seriesRepo = repo;
        this.mapper = new ObjectMapper();


    }

    @Override
    public void save(MultipartFile file) {
        Path path = Path.of(property, fileRoot, file.getOriginalFilename());
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
        try (Stream<Path> files = Files.find(Paths.get(property, fileRoot), 3,
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
    public InputStream write(String filename) {
        writeFile(Series.class, seriesRepo.findAll());

        InputStream stream = load("Series.xlsx");
        findFile("Series.xlsx").delete();

        return stream;
    }

    @Override
    public <E extends BaseEntity, D extends BaseDTO> void load(BiConsumer<IBaseService<E, SeriesDTO>, SeriesDTO> biConsumer, IBaseService<E, SeriesDTO> service) {
        try (FileInputStream file = new FileInputStream(Path.of(property, fileRoot, "Series.xlsx").toFile())) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);


            Field[] fields = SeriesDTO.class.getDeclaredFields();
            List<Cell> cellList = new ArrayList<>();
            List<SeriesDTO> list=service.getAll();
            sheet.forEach(row -> {
                SeriesDTO dto = new SeriesDTO();
                row.forEach(cell -> {
                    if (row.getRowNum() > 0) {
                        List<Field> findFields = Arrays.stream(fields).filter(field1 -> field1.getName().contains(cellList.get(cell.getColumnIndex()).getStringCellValue())).toList();
                        if (!findFields.isEmpty()) {
                            Field field = findFields.get(0);
                            if (field != null) {
                                try {
                                    field.setAccessible(true);
                                    if (field.getType().equals(Number.class)) {
                                        field.set(dto, cell.getNumericCellValue());
                                    } else if (field.getType().equals(LocalDate.class)) {
                                        field.set(dto, LocalDate.parse(cell.getStringCellValue()));
                                    } else if (field.getType().equals(List.class)) {
                                        field.set(dto, mapper.readValue(cell.getStringCellValue(), List.class));
                                    } else {
                                        field.set(dto, cell.getStringCellValue());
                                        field.setAccessible(false);
                                    }
                                } catch (IllegalAccessException | JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        if (cell.getColumnIndex() == cellList.size() - 1)
                            biConsumer.accept(service, dto);
                    } else {
                        cellList.add(cell);
                    }
                });

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends BaseEntity> void createCell(Consumer<Field> consumer, @NotNull Class<T> entityClass) {
        Arrays.stream(entityClass.getDeclaredFields()).forEach(consumer);
    }

    private <T extends BaseEntity> void writeFile(@NotNull Class<T> entityClass, List<T> entities) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Séries");
            for (int i = 0; i < Arrays.stream(entityClass.getDeclaredFields()).toList().size(); i++) {
                sheet.setColumnWidth(i, 6000);
            }


            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);

            Row header = sheet.createRow(0);

            createCell(field -> {
                Cell headerCell = header.createCell((header.getLastCellNum() == -1) ? 0 : header.getLastCellNum());
                headerCell.setCellValue(field.getName());
                headerCell.setCellStyle(headerStyle);
            }, Series.class);

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            entities.forEach(series -> {
                Row row = sheet.createRow((sheet.getLastRowNum() == -1) ? 1 : sheet.getLastRowNum() + 1);
                row.setHeight((short) 6000);
                createCell(field -> {
                    Cell cell = row.createCell((row.getLastCellNum() == -1) ? 0 : row.getLastCellNum());
                    cell.setCellStyle(style);
                    field.setAccessible(true);
                    try {
                        Object value = field.get(series);
                        if (value != null) {
                            if (field.getType().equals(List.class))
                                value = ((List<BaseEntity>) value).stream().map(BaseEntity::getId).toList();
                            cell.setCellValue(value.toString());
                        }

                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    field.setAccessible(false);
                }, Series.class);
            });


            File file = Path.of(property, fileRoot, entityClass.getSimpleName().concat(".xlsx")).toFile();
            try (FileOutputStream stream = new FileOutputStream(file)) {
                workbook.write(stream);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File findFile(String filename){
        return Path.of(property, fileRoot, filename).toFile();
    }
}
