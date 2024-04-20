package fr.perso.springserie.model.dto;

import fr.perso.springserie.model.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {
    @JsonType(type = "id")
    private int id;
}
