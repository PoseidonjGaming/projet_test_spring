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
    private LocalDate datePremDiff;
    private int seasonId;
    private int seriesId;
}
