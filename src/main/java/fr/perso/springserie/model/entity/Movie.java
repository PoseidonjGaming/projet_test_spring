package fr.perso.springserie.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
public class Movie extends BaseEntity {
    private String name;
    private String summary;
    private LocalDate releaseDate;

    @ManyToMany
    private List<Category> category;
    @ManyToMany
    private List<Character> character;
}
