package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.mapper.IMapper;
import org.springframework.stereotype.Service;

@Service
public class CharacterService extends BaseService<Character, CharacterDTO> {
    protected CharacterService(IBaseRepository<Character> repository, IMapper mapper) {
        super(repository, mapper, CharacterDTO.class, Character.class);
    }
}
