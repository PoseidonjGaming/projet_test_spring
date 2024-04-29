package fr.perso.springserie.model.entity;

import fr.perso.springserie.utility.annotation.Entity;
import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document("project")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Json(display = "name")
public class Series extends BaseEntity {
    private String name;
    private LocalDate releaseDate;
    private String summary;
    private String poster;
    private String trailerUrl;
    private String type;
    @DBRef
    private List<Category> category;
    @DBRef
    private List<Character> character;
}
