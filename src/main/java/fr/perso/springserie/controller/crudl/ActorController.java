package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.ActorDTO;
import fr.perso.springserie.service.interfaces.crud.IActorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actor")
public class ActorController extends BaseController<ActorDTO, IActorService> {
    protected ActorController(IActorService service) {
        super(service);
    }
}
