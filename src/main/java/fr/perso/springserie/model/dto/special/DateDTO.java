package fr.perso.springserie.model.dto.special;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateDTO {
    private String field;
    private LocalDate startDate;
    private LocalDate endDate;
}
