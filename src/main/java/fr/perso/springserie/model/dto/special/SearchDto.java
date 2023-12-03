package fr.perso.springserie.model.dto.special;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.ExampleMatcher;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
    private ExampleMatcher.MatchMode mode;
    private ExampleMatcher.StringMatcher type;
}
