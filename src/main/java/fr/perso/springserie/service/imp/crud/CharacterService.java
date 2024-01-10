package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.crud.ICharacterService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class CharacterService extends BaseService<Character, CharacterDTO> implements ICharacterService {
    protected CharacterService(IBaseRepo<Character> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, CharacterDTO.class, Character.class, mapService);
    }
}
