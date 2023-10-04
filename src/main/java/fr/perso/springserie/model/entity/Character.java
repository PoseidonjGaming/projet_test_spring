package fr.perso.springserie.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class Character extends BaseEntity {

    private String name;

    @ManyToMany(mappedBy = "character")
    private List<Series> series;
    @ManyToMany(mappedBy = "character")
    private List<Movie> movie;
    @ManyToOne
    private Actor actor;
}
