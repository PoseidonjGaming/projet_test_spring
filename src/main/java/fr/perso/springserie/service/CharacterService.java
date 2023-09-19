package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.CharacterDTO;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.repository.IActorRepo;
import fr.perso.springserie.repository.ICharacterRepo;
import fr.perso.springserie.repository.ISeriesRepo;
import fr.perso.springserie.service.interfaces.ICharacterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterService extends BaseService<Character, CharacterDTO> implements ICharacterService {

    private final IActorRepo actorRepo;
    private final ISeriesRepo seriesRepo;

    protected CharacterService(ICharacterRepo repository, IActorRepo actorRepo, ISeriesRepo seriesRepo) {
        super(repository, CharacterDTO.class, Character.class);
        this.actorRepo = actorRepo;
        this.seriesRepo = seriesRepo;
    }

    @Override
    public List<CharacterDTO> search(String term) {
        return null;
    }

    @Override
    public Character toEntity(CharacterDTO dto) {
        Character entity = super.toEntity(dto);
        entity.setActor(dto.getActorIds().stream().map(e -> actorRepo.findById(e).orElse(null)).toList());
        entity.setSeries(dto.getSeriesIds().stream().map(e -> seriesRepo.findById(e).orElse(null)).toList());
        return entity;
    }
}
