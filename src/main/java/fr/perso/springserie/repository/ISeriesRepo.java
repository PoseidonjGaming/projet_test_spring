package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.Series;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISeriesRepo extends IBaseRepo<Series>{
    List<Series> findByProjectCategoryIn(List<Integer> categoryIds);
}
