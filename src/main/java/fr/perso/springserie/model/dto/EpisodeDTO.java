package fr.perso.springserie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDTO extends BaseDTO {
    private String name;
    private String summary;
    private LocalDate releaseDate;
    private Integer seasonId;
    private Integer seriesId;
}
