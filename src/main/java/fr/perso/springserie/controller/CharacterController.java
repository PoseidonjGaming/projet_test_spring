package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.service.interfaces.ICharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/character")
public class CharacterController extends BaseController<Character, CharacterDTO> {

    @Autowired
    public CharacterController(ICharacterService service) {
        super(service);
    }

}
