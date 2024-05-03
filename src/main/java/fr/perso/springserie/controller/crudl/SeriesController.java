package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController extends BaseController<SeriesDTO, IBaseService<SeriesDTO>> {

    protected SeriesController(IBaseService<SeriesDTO> service) {
        super(service);
    }


    @GetMapping("/test")
    public ResponseEntity<Boolean> test() {
        List<String> searchIds = new ArrayList<>();
        List<String> ids = List.of("id1", "id2");
        return ResponseEntity.ok(ids.containsAll(searchIds));
    }
}
