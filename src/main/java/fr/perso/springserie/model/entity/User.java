package fr.perso.springserie.model.entity;

import fr.perso.springserie.utility.annotation.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Indexed(unique = true)
    private String username;
    private List<String> roles;
    private String password;
    private String avatarFile;
    @DBRef
    private List<Review> review;
    @DBRef
    private List<Series> seriesWatchList;
    @DBRef
    private List<Movie> moviesWatchlist;
}
