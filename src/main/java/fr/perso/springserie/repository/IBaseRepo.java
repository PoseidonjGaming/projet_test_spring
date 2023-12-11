package fr.perso.springserie.repository;

import fr.perso.springserie.model.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public  interface IBaseRepo<E extends BaseEntity> extends JpaRepository<E, Integer> {
    List<E> findByIdIn(List<Integer> ids);

    Page<E> findByIdIn(List<Integer> ids, Pageable pageable);
}
