package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;

import java.util.List;

public interface IUserService extends IBaseService<UserDTO> {
    JwtResponse authenticate(JwtUser user);

    void registration(UserDTO userDTO);

    List<SeriesDTO> addToWatchList(int seriesId, String username);
    List<SeriesDTO> removeFromWatchList(int seriesId, String username);
}
