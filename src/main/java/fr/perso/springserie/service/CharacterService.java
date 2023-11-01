package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.repository.ICharacterRepo;
import fr.perso.springserie.service.interfaces.ICharacterService;
import org.springframework.stereotype.Service;

@Service
public class CharacterService extends BaseService<Character, CharacterDTO> implements ICharacterService {


    protected CharacterService(ICharacterRepo repository) {
        super(repository, CharacterDTO.class, Character.class);
    }

}
