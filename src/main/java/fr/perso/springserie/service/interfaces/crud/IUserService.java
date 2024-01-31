package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;

import java.util.List;

public interface IUserService extends IBaseService<UserDTO> {
    JwtResponse authentication(JwtUser user);

    void registration(UserDTO userDTO);

    List<Integer> addToWatchList(String type, int id, String username);

    List<Integer> removeFromWatchList(String type, Integer seriesId, String username);

    UserDTO searchByUsername(String username);
}
