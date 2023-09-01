package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.ActeurDTO;
import fr.perso.springserie.model.entity.Acteur;
import fr.perso.springserie.service.interfaces.IActeurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actor")
public class ActeurController extends BaseController<Acteur, ActeurDTO> {

    @Autowired
    public ActeurController(IActeurService service) {
        super(service);
    }
}
