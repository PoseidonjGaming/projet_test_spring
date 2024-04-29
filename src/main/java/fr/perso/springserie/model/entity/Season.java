package fr.perso.springserie.model.entity;

import fr.perso.springserie.utility.annotation.Entity;
import fr.perso.springserie.utility.annotation.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
@CompoundIndex(name="season_unx",def = "{'seriesId': 1, 'number': 1}", unique = true)
@Json(display = "number")
public class Season extends BaseEntity {
    private String seriesId;
    private Integer number;
}
