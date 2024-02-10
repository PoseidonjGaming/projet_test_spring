package fr.perso.springserie.service.utility;

import fr.perso.springserie.interceptor.exception.FileException;
import jxl.write.Number;
import jxl.write.*;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class FileUtility {

    public static void createFolder(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new FileException(e);
            }
        }
    }
}
