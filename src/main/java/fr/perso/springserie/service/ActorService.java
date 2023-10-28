package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.ActorDTO;
import fr.perso.springserie.model.entity.Actor;
import fr.perso.springserie.repository.IActorRepo;
import fr.perso.springserie.service.interfaces.IActorService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ActorService extends BaseService<Actor, ActorDTO> implements IActorService {
    protected ActorService(IActorRepo repository) {
        super(repository, ActorDTO.class, Actor.class);
    }

    @Override
    public List<ActorDTO> search(String lastname) {
        return ((IActorRepo) repository).findByLastnameContaining(lastname).stream().map(this::toDTO).toList();
    }

    @Override
    public List<ActorDTO> test(ActorDTO dto) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withIgnoreNullValues().withIgnorePaths("id");

        Arrays.stream(Actor.class.getDeclaredFields()).forEach(field -> {
          if(field.getType().equals(String.class)){
              matcher(exampleMatcher, exampleMatcher1 -> exampleMatcher1.withMatcher(field.getName(),
                      ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()));
          }
        });
        List<Actor> entities = repository.findAll(Example.of(toEntity(dto), exampleMatcher));
        return toDTOList(entities);
    }

    private void matcher(ExampleMatcher exampleMatcher, Consumer<ExampleMatcher> consumer){
        consumer.accept(exampleMatcher);
    }
}
