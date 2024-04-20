package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "\"character\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonType(display = "name")
public class Character extends BaseEntity {

    private String name;

    @ManyToMany(mappedBy = "project.character", fetch = FetchType.EAGER)
    private List<Series> series;
    @ManyToMany(mappedBy = "project.character", fetch = FetchType.EAGER)
    private List<Movie> movie;
    @ManyToOne
    private Actor actor;
}
