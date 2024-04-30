package fr.perso.springserie.model.entity;

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
@Json(display = "userId")
public class Review extends BaseEntity {
    private Integer note;
    private String comment;
    private String userId;
    private String seriesId;
    private String movieId;
}
