package fr.perso.springserie.model.dto;

import fr.perso.springserie.utility.annotation.Json;
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
    @Json(type = "review")
    private List<String> reviewIds;
    @Json(type = "series")
    private List<String> seriesWatchList;
    @Json(type = "movie")
    private List<String> moviesWatchlist;

    public UserDTO(String username) {
        this.username = username;
        roles = new ArrayList<>();
    }
}
