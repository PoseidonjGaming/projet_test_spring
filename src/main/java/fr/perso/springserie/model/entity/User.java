package fr.perso.springserie.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(unique = true)
    private String username;
    @Column(columnDefinition = "json")
    private String roles;
    private String password;
    private String avatarFile;
}
