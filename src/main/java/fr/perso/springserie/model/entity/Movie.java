package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.embeddable.ProjectEmbeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie extends BaseEntity {
    @Embedded
    private ProjectEmbeddable project;
}
