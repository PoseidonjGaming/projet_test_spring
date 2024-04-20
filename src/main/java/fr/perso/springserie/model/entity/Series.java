package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.JsonType;
import fr.perso.springserie.model.embeddable.ProjectEmbeddable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonType(display = "name")
public class Series extends BaseEntity {

    @Embedded
    private ProjectEmbeddable project;

    @OneToMany(mappedBy = "series", fetch = FetchType.EAGER,cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Season> season;


}
