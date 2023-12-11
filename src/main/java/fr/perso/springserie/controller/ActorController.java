package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.ActorDTO;
import fr.perso.springserie.service.interfaces.crud.IActorService;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actor")
public class ActorController extends BaseController<ActorDTO>{
    protected ActorController(IActorService service) {
        super(service);
    }
}
