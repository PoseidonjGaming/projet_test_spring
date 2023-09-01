package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.User;

import java.util.Optional;

public interface IUserRepo extends IBaseRepo<User> {
    Optional<User> findByUsername(String nom);
}
