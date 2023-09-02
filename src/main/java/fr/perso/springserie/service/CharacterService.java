package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.repository.IActorRepo;
import fr.perso.springserie.repository.ICharacterRepo;
import fr.perso.springserie.service.interfaces.ICharacterService;
import org.springframework.stereotype.Service;

@Service
public class CharacterService extends BaseService<Character, CharacterDTO> implements ICharacterService {


    private final IActorRepo acteurRepo;

    protected CharacterService(ICharacterRepo repository, IActorRepo acteurRepo) {
        super(repository, CharacterDTO.class, Character.class);
        this.acteurRepo = acteurRepo;
    }

    @Override
    public CharacterDTO toDTO(Character entity) {
        CharacterDTO dto = super.toDTO(entity);
        dto.setActorsIds(map(entity.getActor()));
        dto.setSeriesIds(map(entity.getSeries()));
        return dto;
    }
}
