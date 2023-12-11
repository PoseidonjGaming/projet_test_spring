package fr.perso.springserie.model.dto.special;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortDTO {
    private String field;
    private Sort.Direction direction;
}
