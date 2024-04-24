package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.JsonType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"character\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonType(display = "name")
public class Character extends BaseEntity {

    private String name;

    @ManyToOne
    private Actor actor;
}
