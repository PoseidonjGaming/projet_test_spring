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
public class EpisodeDTO extends BaseDTO {
    private String name;
    @Json(type = "text")
    private String summary;
    private LocalDate releaseDate;
    @Json(type = "season", display = "number")
    private String seasonId;
}
