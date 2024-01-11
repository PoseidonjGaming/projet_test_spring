package fr.perso.springserie.model.dto;

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
    private LocalDate releaseDate;
    private String summary;
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
