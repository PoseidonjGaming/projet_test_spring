package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.BaseEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface IBaseRepository<E extends BaseEntity> extends MongoRepository<E, String>{
    List<E> findByIdIn(List<String> ids);

    List<E> findByIdIn(List<String> ids, Example<E> example);

    Page<E> findByIdIn(List<String> ids, Pageable pageable);

    Page<E> findByIdIn(List<String> ids, Example<E> example, Pageable pageable);
}
