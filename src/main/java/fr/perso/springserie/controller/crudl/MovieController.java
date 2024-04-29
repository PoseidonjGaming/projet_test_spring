package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.MovieDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
public class MovieController extends BaseController<MovieDTO, IBaseService<MovieDTO>> {
    protected MovieController(IBaseService<MovieDTO> service) {
        super(service);
    }
}
