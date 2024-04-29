package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.MovieDTO;
import fr.perso.springserie.model.entity.Movie;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

@Service
public class MovieService extends BaseService<Movie, MovieDTO> {
    protected MovieService(IBaseRepository<Movie> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, MovieDTO.class, Movie.class, mapService);
    }
}
