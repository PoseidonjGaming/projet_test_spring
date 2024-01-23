package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.MovieDTO;
import fr.perso.springserie.service.interfaces.crud.IMovieService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
public class MovieController extends BaseController<MovieDTO, IMovieService> {
    protected MovieController(IMovieService service) {
        super(service);
    }
}
