package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.repository.IUserRepository;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.service.interfaces.crud.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController<UserDTO, IUserService> {

    private final IUserRepository userRepository;

    protected UserController(IUserService service, IUserRepository userRepository) {
        super(service);
        this.userRepository = userRepository;
    }


    @GetMapping("/search/{username}")
    public ResponseEntity<UserDTO> getByUsername(@PathVariable String username) {
        return ResponseEntity.ofNullable(service.searchByUsername(username));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody JwtUser user) {
        return ResponseEntity.ofNullable(service.authentication(user));
    }

    @PostMapping("/registration")
    public void registration(@RequestBody UserDTO user) {
        service.registration(user);
    }

    @GetMapping("/test")
    public ResponseEntity<List<User>> test() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
