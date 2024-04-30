package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.ActorDTO;
import fr.perso.springserie.model.entity.Actor;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.mapper.IMapper;
import org.springframework.stereotype.Service;

@Service
public class ActorService extends BaseService<Actor, ActorDTO> {
    protected ActorService(IBaseRepository<Actor> repository, IMapper mapper) {
        super(repository, mapper, ActorDTO.class, Actor.class);
    }
}
