package fr.perso.springserie.model.embeddable;

import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.model.entity.Character;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProjectEmbeddable implements Serializable {
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
}
