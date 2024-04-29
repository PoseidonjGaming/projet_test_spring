package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.Movie;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovieRepository extends IBaseRepository<Movie> {
}
