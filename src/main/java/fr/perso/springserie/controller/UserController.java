package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.service.interfaces.crud.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController<UserDTO, IUserService> {
    protected UserController(IUserService service) {
        super(service);
    }

    @Override
    protected Consumer<UserDTO> getConsumer() {
        return UserDTO::erasePassword;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody JwtUser user) {
        return ResponseEntity.ofNullable(service.authenticate(user));
    }

    @PostMapping("/registration")
    public void registration(@RequestBody UserDTO user) {
        service.registration(user);
    }

    @GetMapping("/add/watch/{seriesId}")
    public ResponseEntity<List<SeriesDTO>> addWatch(@PathVariable int seriesId, String username){
        return ResponseEntity.ok(service.addToWatchList(seriesId, username));
    }

    @GetMapping("/remove/watch/{seriesId}")
    public ResponseEntity<List<SeriesDTO>> removeWatch(@PathVariable int seriesId, String username){
        return ResponseEntity.ok(service.removeFromWatchList(seriesId, username));
    }
}
