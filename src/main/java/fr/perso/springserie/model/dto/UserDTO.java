package fr.perso.springserie.model.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        roles = "";
    }

    public List<String> getRoles() {
        Gson gson = new Gson();
        return gson.fromJson(roles, List.class);
    }

    public void setRoles(List<String> roles) {
        this.roles = roles.toString();
    }

    public void erasePassword() {
        password = "";
    }
}
