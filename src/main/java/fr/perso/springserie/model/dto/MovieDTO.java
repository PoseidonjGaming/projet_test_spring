package fr.perso.springserie.model.dto;

import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.model.entity.Character;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class MovieDTO extends BaseDTO {
    private String name;
    private String summary;
    private LocalDate releaseDate;

    private List<Integer> categoryIds;
    private List<Integer> characterIds;
}
