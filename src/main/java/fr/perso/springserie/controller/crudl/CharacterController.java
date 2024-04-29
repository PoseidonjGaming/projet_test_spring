package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/character")
public class CharacterController extends BaseController<CharacterDTO, IBaseService<CharacterDTO>> {
    protected CharacterController(IBaseService<CharacterDTO> service) {
        super(service);
    }
}
