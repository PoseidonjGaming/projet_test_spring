package fr.perso.springserie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends BaseDTO {
    private String username;
    private String roles;
    private String password;
    private String avatarFile;

    public UserDTO(String username) {
        this.username = username;
        roles="";
    }

    public List<String> getRoles() {
        return Arrays.stream(roles.split(",")).map(e -> e.replace("[", "")
                .replace("\"", "").replace("]", "")).toList();
    }

    public void setRoles(List<String> roles) {
        this.roles = roles.toString();
    }
}
