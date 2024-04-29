package fr.perso.springserie.model.dto;

import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO extends BaseDTO {
    private String name;
    private LocalDate releaseDate;
    @Json(type = "text")
    private String summary;
    private String poster;
    private String trailerUrl;

}
