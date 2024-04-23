package fr.perso.springserie.model.dto;

import fr.perso.springserie.model.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeriesDTO extends BaseDTO {
    private String name;
    @JsonType(type = "text")
    private String summary;
    private LocalDate releaseDate;
    @JsonType(type = "file")
    private String poster;
    private String trailerUrl;
    private Integer nextMovieId;
    private Integer nextSeriesId;
    private Integer previousMovieId;
    private Integer previousSeriesId;
    private List<Integer> seasonIds;
    private List<Integer> categoryIds;
    private List<Integer> characterIds;
}
