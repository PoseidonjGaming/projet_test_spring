package fr.perso.springserie.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "project.category")
    private List<Series> series;

    @ManyToMany(mappedBy = "project.category")
    private List<Movie> movie;
}
