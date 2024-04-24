package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.service.interfaces.crud.ISeasonService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.perso.springserie.service.utility.SearchUtility.findField;
import static fr.perso.springserie.service.utility.SearchUtility.getPath;

@RestController
@RequestMapping("/season")
public class SeasonController extends BaseController<SeasonDTO, ISeasonService> {
    private final ISeasonRepo repository;
    protected SeasonController(ISeasonService service, ISeasonRepo repository) {
        super(service);
        this.repository = repository;
    }


}
