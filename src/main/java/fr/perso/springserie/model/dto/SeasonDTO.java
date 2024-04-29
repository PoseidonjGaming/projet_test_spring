package fr.perso.springserie.model.dto;

import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.utility.annotation.Entity;
import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeasonDTO extends BaseDTO {
    @Json(type = "series", display = "name")
    private String seriesId;
    private Integer number;
}
