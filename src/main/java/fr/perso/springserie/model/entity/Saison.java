package fr.perso.springserie.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"serie","numero"}))
public class Saison extends BaseEntity {

    @ManyToOne
    private Series serie;
    private int numero;

    @OneToMany(mappedBy = "saison")
    private List<Episode> episodes;
}
