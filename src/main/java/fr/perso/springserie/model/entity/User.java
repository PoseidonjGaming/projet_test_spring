package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@JsonType(display = "username")
public class User extends BaseEntity {
    @Column(unique = true)
    private String username;
    @Column(columnDefinition = "json")
    private String roles;
    private String password;
    private String avatarFile;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Series> series;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Movie> movie;
}
