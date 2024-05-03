package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/episode")
public class EpisodeController extends BaseController<EpisodeDTO, IBaseService<EpisodeDTO>> {
    protected EpisodeController(IBaseService<EpisodeDTO> service) {
        super(service);
    }


}
