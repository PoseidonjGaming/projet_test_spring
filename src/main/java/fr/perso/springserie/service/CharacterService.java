package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.repository.ICharacterRepo;
import fr.perso.springserie.service.interfaces.ICharacterService;
import fr.perso.springserie.service.interfaces.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

@Service
public class CharacterService extends BaseService<Character, CharacterDTO> implements ICharacterService {


    protected CharacterService(ICharacterRepo repository, MapService mapService, IMapper<Character,CharacterDTO> customMapper) {
        super(repository, CharacterDTO.class, Character.class, mapService, customMapper);
    }

}
