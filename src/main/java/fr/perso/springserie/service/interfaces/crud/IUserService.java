package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends IBaseService<UserDTO>, UserDetailsService {
    JwtResponse authentication(JwtUser user);

    void registration(UserDTO userDTO);

    List<Integer> addToWatchList(String type, int id, String username);

    List<Integer> removeFromWatchList(String type, String seriesId, String username);

    UserDTO searchByUsername(String username);

    void addWatchlist(String username, String seriesId, String type);
}
