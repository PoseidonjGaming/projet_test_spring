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
public class SeriesDTO extends BaseDTO{
    private String nom;
    private LocalDate dateDiff;
    private String resume;
    private String affiche;
    private String urlBa;
    private List<Integer> saisonsIds;
}
