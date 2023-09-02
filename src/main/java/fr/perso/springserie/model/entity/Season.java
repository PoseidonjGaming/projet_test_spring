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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"series_id","number"}))
public class Season extends BaseEntity {

    @ManyToOne
    private Series series;
    private int number;

    @OneToMany(mappedBy = "season")
    private List<Episode> episodes;
}
