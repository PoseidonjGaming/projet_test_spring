package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.embeddable.ProjectEmbeddable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Series extends BaseEntity {

    @Embedded
    private ProjectEmbeddable project;

    @OneToMany(mappedBy = "series", fetch = FetchType.EAGER)
    private List<Season> season;



}
