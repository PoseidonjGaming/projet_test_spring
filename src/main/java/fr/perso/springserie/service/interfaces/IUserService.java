package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;

public interface IUserService extends IBaseService<UserDTO> {
    JwtResponse authenticate(JwtUser user);
}
