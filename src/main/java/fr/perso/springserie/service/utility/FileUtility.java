package fr.perso.springserie.service.utility;

import fr.perso.springserie.model.dto.BaseDTO;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static fr.perso.springserie.service.utility.ServiceUtility.browseField;
import static fr.perso.springserie.service.utility.ServiceUtility.get;

@UtilityClass
public class FileUtility {

    public static void createFolder(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints(Short.parseShort("16"));
        font.setBold(true);

        style.setFont(font);
        return style;
    }

    public static void createHeader(Row headers, CellStyle headerStyle, Class<?> clazz) {
        browseField(clazz, field -> {
            int cellIndex = (headers.getLastCellNum() == -1) ? 0 : headers.getLastCellNum();
            Cell header = headers.createCell(cellIndex);
            header.setCellStyle(headerStyle);
            header.setCellValue(field.getName());
        });
    }

    public static void createRowValue(BaseDTO baseDTO, Row row) {
        browseField(baseDTO.getClass(), field -> {
            int cellIndex = (row.getLastCellNum() == -1) ? 0 : row.getLastCellNum();
            Cell rowCell = row.createCell(cellIndex);

            Object value = get(field, baseDTO);
            if (value == null) {
                value = "null";
            }
            rowCell.setCellValue(value.toString());
        });
    }

    public void writeToFile(Workbook workbook, String root, String resourcePath) {
        Path path = Paths.get(root, resourcePath, "files");
        createFolder(path);
        try (FileOutputStream outputStream = new FileOutputStream(Paths.get(path.toString(), "data.xlsx").toFile())) {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
