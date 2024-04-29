package fr.perso.springserie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends BaseDTO {
    private String username;
    private List<String> roles;
    private String password;
    private String avatarFile;

    public UserDTO(String username) {
        this.username = username;
        roles = new ArrayList<>();
    }
}
