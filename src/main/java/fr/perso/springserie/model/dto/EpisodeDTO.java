package fr.perso.springserie.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EpisodeDTO extends BaseDTO {
    private String nom;
    private String resume;
    private LocalDate datePremDiff;
    private int saisonId;
    private int seriesId;
}
