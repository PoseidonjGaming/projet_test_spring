package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.service.UserService;
import fr.perso.springserie.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController<User, UserDTO> {

    @Autowired
    public UserController(UserService service) {
        super(service);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody JwtUser user) {
        return ResponseEntity.ofNullable(((IUserService) service).authenticate(user));
    }

    @GetMapping("/generateuser")
    public ResponseEntity<?> generate() {
        service.save(new UserDTO("Admin",List.of("ROLE_super_admin").toString(),"1234"));
        return ResponseEntity.ok().build();
    }
}
