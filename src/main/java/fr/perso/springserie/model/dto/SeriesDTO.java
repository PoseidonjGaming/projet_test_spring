package fr.perso.springserie.model.dto;

import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeriesDTO extends BaseDTO {
    private String name;
    private LocalDate releaseDate;
    @Json(type = "text")
    private String summary;
    @Json(type = "file")
    private String poster;
    private String trailerUrl;
    @Json(type = "category", display = "name")
    private List<String> categoryIds;
    @Json(type = "character", display = "name")
    private List<String> characterIds;
}
