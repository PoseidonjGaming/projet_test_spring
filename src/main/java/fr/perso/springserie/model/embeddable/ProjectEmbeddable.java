package fr.perso.springserie.model.embeddable;

import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.model.entity.Movie;
import fr.perso.springserie.model.entity.Series;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProjectEmbeddable {
    @Column(nullable = false)
    private String name;
    private LocalDate releaseDate;
    @Column(columnDefinition = "text")
    private String summary;
    private String poster;
    private String trailerUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Category> category;
    @ManyToMany(fetch = FetchType.EAGER)
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
