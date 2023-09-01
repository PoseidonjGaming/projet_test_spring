package fr.perso.springserie.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    private String username;
    @Column(columnDefinition = "json")
    private String roles;
    private String password;

    public List<String> getRoles() {
        return Arrays.stream(roles.split(",")).map(e->e.replace("[", "")
                .replace("\"", "").replace("]","")).collect(Collectors.toList());
    }
}
