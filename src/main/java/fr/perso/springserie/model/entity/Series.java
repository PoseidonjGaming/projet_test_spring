package fr.perso.springserie.model.entity;

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
    private String name;
    private LocalDate releaseDate;

    @Column(columnDefinition = "text")
    private String summary;
    private String poster;
    private String trailerUrl;

    @OneToMany(mappedBy = "series",fetch = FetchType.EAGER)
    private List<Season> seasons;

    @ManyToMany
    private List<Character> character;

    @ManyToMany
    private List<Category> category;


}
