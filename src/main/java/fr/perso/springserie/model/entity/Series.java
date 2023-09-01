package fr.perso.springserie.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "serie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Series extends BaseEntity {
    private String nom;
    private LocalDate dateDiff;

    @Column(columnDefinition = "text")
    private String resume;
    private String affiche;
    private String urlBa;


}
