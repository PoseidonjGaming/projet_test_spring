package fr.perso.springserie.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Episode extends BaseEntity{

    private String nom;

    @Column(columnDefinition = "text")
    private String resume;

    private LocalDate datePremDiff;

    @ManyToOne
    private Saison saison;

}
