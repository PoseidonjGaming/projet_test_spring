package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.MovieDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.Movie;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.crud.IMovieService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class MovieService extends BaseService<Movie, MovieDTO> implements IMovieService {
    protected MovieService(IBaseRepo<Movie> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, MovieDTO.class, Movie.class, mapService);
    }

    @Override
    protected Predicate<MovieDTO> predicate(SearchDTO<MovieDTO> searchDTO) {
        return null;
    }
}
