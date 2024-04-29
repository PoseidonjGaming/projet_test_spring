package fr.perso.springserie.model.dto;

import fr.perso.springserie.utility.annotation.Entity;
import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDTO extends BaseDTO {
    private String name;
    @Json(type = "actor",display = "lastname")
    private String actorId;
}
