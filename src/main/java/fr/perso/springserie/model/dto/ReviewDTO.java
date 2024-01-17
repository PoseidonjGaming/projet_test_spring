package fr.perso.springserie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO extends BaseDTO {
    private Integer note;
    private String comment;
    private Integer userId;
    private Integer seriesId;
}
