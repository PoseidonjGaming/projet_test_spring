package fr.perso.springserie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO extends BaseDTO {
    private String name;
    private Integer actorId;
    private List<Integer> seriesIds;
    private List<Integer> movieIds;
}
