package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.service.interfaces.crud.ISeasonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/season")
public class SeasonController extends BaseController<SeasonDTO, ISeasonService> {
    protected SeasonController(ISeasonService service) {
        super(service);
    }
}
