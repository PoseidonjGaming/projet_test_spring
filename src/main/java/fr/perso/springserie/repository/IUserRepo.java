package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.User;

import java.util.List;

public interface IUserRepo extends IBaseRepo<User> {
    List<User> findByUsernameContains(String nom);
}
