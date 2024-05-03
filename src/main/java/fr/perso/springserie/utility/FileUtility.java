package fr.perso.springserie.utility;

import fr.perso.springserie.interceptor.exception.GenericException;
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
                throw new GenericException(e);
            }
        }
    }
}
