package fr.perso.springserie.model.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NamedReviewDTO {
    private Integer note;
    private String comment;
    private Integer username;
    private Integer seriesName;
}
