package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.Saison;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISaisonRepo extends IBaseRepo<Saison> {
    List<Saison> findBySerieId(int id);
}
