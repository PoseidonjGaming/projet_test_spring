package fr.perso.springserie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IBaseRepo<E> extends JpaRepository<E, Integer> {
}
