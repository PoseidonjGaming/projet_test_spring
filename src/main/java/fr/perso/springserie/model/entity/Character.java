package fr.perso.springserie.model.entity;

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
@AllArgsConstructor
@NoArgsConstructor
public class Character extends BaseEntity{

    private String name;

    @ManyToMany
    private List<Series> series;
    @ManyToMany(mappedBy = "characters")
    private List<Actor> actors;
}
