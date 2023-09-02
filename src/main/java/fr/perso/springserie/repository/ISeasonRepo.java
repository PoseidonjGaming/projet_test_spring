package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.Season;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISeasonRepo extends IBaseRepo<Season> {
    List<Season> findBySeriesId(int id);
}
