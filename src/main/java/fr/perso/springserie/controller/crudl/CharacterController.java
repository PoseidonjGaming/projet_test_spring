package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.service.interfaces.crud.ICharacterService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/character")
public class CharacterController extends BaseController<CharacterDTO, ICharacterService> {
    protected CharacterController(ICharacterService service) {
        super(service);
    }
}
