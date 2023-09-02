package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.ActorDTO;
import fr.perso.springserie.model.entity.Actor;
import fr.perso.springserie.repository.IActorRepo;
import fr.perso.springserie.service.interfaces.IActorService;
import org.springframework.stereotype.Service;

@Service
public class ActorService extends BaseService<Actor, ActorDTO> implements IActorService {
    protected ActorService(IActorRepo repository) {
        super(repository, ActorDTO.class, Actor.class);
    }
}
