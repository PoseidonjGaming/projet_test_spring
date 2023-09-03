package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.Actor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IActorRepo extends IBaseRepo<Actor> {
    List<Actor> findByLastnameContaining(String lastname);
}
