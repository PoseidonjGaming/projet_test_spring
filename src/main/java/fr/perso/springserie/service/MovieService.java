package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.MovieDTO;
import fr.perso.springserie.model.entity.Movie;
import fr.perso.springserie.repository.IMovieRepo;
import fr.perso.springserie.service.interfaces.IMovieService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService extends BaseService<Movie, MovieDTO> implements IMovieService {
    protected MovieService(IMovieRepo repository) {
        super(repository, MovieDTO.class, Movie.class);
    }

}
