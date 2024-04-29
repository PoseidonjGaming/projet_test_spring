package fr.perso.springserie.model.entity;

import fr.perso.springserie.utility.annotation.Entity;
import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("project")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Json(display = "name")
public class Movie extends BaseEntity {
    private String name;
    private LocalDate releaseDate;
    private String summary;
    private String poster;
    private String trailerUrl;

}
