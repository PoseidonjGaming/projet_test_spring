package fr.perso.springserie.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Component
public class Startup implements CommandLineRunner {

    @Autowired
    private MapService mapService;

    private String[] order = {"user", "actor", "character", "series", "season", "episode"};

    @Override
    public void run(String... args) {


        try (Stream<Path> files = Files.list(Paths.get(System.getProperty("user.dir"), "src/main/resources/data"))) {
            List<Path> paths = files.toList();
            for (String s : order) {
                paths.stream().filter(path -> path.getFileName().toString().startsWith(s)).findFirst().ifPresent(path ->
                        mapService.getService(s).save(path.toFile()));
            }
            System.out.println("after");
        } catch (IOException ignored) {

        }
    }
}
