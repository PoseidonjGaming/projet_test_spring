package fr.perso.springserie.model.entity;

import fr.perso.springserie.model.JsonType;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonType(display = "name")
public class Actor extends BaseEntity {
    private String lastname;
    private String firstname;
}
