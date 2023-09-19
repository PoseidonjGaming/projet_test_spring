package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.ActorDTO;
import fr.perso.springserie.model.entity.Actor;
import fr.perso.springserie.service.interfaces.IActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actor")
public class ActorController extends BaseController<Actor, ActorDTO> {

    @Autowired
    public ActorController(IActorService service) {
        super(service);
    }

}
