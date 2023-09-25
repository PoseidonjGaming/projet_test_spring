package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.Category;

import java.util.List;

public interface ICategoryRepo extends IBaseRepo<Category> {
    List<Category> findByNameContains(String term);
}
