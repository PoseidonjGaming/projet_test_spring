package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.JsonType;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"series_id", "number"}))
@JsonType(display = "number")
public class Season extends BaseEntity {

    @ManyToOne
    private Series series;
    private Integer number;

    @OneToMany(mappedBy = "season", fetch = FetchType.EAGER)
    private List<Episode> episode;
}
