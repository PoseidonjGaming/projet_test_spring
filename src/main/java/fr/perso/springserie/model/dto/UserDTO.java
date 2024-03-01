package fr.perso.springserie.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
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
    private List<Integer> seriesIds;
    private List<Integer> movieIds;

    public UserDTO(String username) {
        this.username = username;
        roles = "";
    }

    public List<String> getRoles() {
        Gson gson = new Gson();
        return gson.fromJson(roles, new TypeToken<List<String>>(){}.getType());
    }

    public void setRoles(List<String> roles) {
        this.roles = new Gson().toJson(roles);
    }

    public void erasePassword() {
        password = "";
    }
}
