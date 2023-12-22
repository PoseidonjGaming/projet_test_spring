package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.ActorDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.Actor;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.crud.IActorService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class ActorService extends BaseService<Actor, ActorDTO> implements IActorService {
    protected ActorService(IBaseRepo<Actor> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, ActorDTO.class, Actor.class, mapService);
    }

    @Override
    protected Predicate<ActorDTO> predicate(SearchDTO<ActorDTO> searchDTO) {
        return null;
    }
}
