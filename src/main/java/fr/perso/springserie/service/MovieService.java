package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.MovieDTO;
import fr.perso.springserie.model.entity.Movie;
import fr.perso.springserie.repository.IMovieRepo;
import fr.perso.springserie.service.interfaces.IMovieService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

@Service
public class MovieService extends BaseService<Movie, MovieDTO> implements IMovieService {
    protected MovieService(IMovieRepo repository, MapService mapService, IMapper customMapper) {
        super(repository, MovieDTO.class, Movie.class, mapService, customMapper);
    }

}
