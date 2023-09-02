package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.Episode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEpisodeRepo extends IBaseRepo<Episode> {
    List<Episode> findBySeasonIdIn(List<Integer> id);
}
