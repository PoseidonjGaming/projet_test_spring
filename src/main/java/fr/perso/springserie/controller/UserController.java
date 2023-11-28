package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController<User, UserDTO> {

    @Autowired
    public UserController(IUserService service) {
        super(service);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody JwtUser user) {
        return ResponseEntity.ofNullable(((IUserService) service).authenticate(user));
    }


}
