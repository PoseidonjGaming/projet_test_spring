package fr.perso.springserie.model.dto.special;

import fr.perso.springserie.model.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.ExampleMatcher;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO<D extends BaseDTO> {

    private D dto;

    private ExampleMatcher.MatchMode mode;
    private ExampleMatcher.StringMatcher type;

    private LocalDate startDate;
    private LocalDate endDate;
}
