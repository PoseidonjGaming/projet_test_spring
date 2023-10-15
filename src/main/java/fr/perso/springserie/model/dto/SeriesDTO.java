package fr.perso.springserie.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class SeriesDTO extends BaseDTO {
    private String name;
    private LocalDate releaseDate;
    private String summary;
    private String poster;
    private String trailerUrl;
    private int nextMovieId;
    private int nextSeriesId;
    private int previousMovieId;
    private int previousSeriesId;
    private List<Integer> seasonsIds;
    private List<Integer> categoryIds;
    private List<Integer> characterIds;
}
