package fr.perso.springserie.repository;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISeriesRepo extends IBaseRepo<Series> {
    List<Series> findByNameContaining(String name);
    List<Series> findByCategoryIn(List<Integer> categoryIds);
    List<Series> findByNameContainingAndCategoryIn(String term, List<Integer> categoryIds);
}
