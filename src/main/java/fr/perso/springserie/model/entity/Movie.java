package fr.perso.springserie.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
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
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "text")
    private String summary;
    private LocalDate releaseDate;

    @ManyToMany
    private List<Category> category;
    @ManyToMany
    private List<Character> character;
    @OneToOne
    private Series nextSeries;
    @OneToOne
    private Series previousSeries;
    @OneToOne
    private Movie nextMovie;
    @OneToOne
    private Movie previousMovie;
}
