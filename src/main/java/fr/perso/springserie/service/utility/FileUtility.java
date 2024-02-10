package fr.perso.springserie.service.utility;

import fr.perso.springserie.interceptor.exception.FileException;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
