package fr.perso.springserie.controller.crudl;

import com.mongodb.BasicDBList;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.service.mapper.IMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController extends BaseController<SeriesDTO, IBaseService<SeriesDTO>> {

    protected SeriesController(IBaseService<SeriesDTO> service) {
        super(service);
    }


}
