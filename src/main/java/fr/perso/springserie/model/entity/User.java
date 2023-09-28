package fr.perso.springserie.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

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

    public List<String> getRoles() {
        return Arrays.stream(roles.split(",")).map(e -> e.replace("[", "")
                .replace("\"", "").replace("]", "")).toList();
    }

    public void setRoles(List<String> roles) {
        this.roles = new JSONArray(roles).toString();
    }
}
