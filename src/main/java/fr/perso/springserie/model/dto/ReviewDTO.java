package fr.perso.springserie.model.dto;

import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO extends BaseDTO {
    private Integer note;
    @Json(type = "text")
    private String comment;
    @Json(type = "user", display = "username")
    private String userId;
    @Json(type = "series",display = "name")
    private String seriesId;
    @Json(type = "movie", display = "name")
    private String movieId;
}
