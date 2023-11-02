package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.repository.IUserRepo;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.security.JwtUtil;
import fr.perso.springserie.service.interfaces.IUserService;
import fr.perso.springserie.task.MapService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService extends BaseService<User, UserDTO> implements IUserService, UserDetailsService {

    protected final JwtUtil jwtTokenUtil;
    protected final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    @Lazy
    public UserService(IUserRepo repo, MapService mapService, JwtUtil jwtTokenUtil,
                       AuthenticationManager authenticationManager, PasswordEncoder encoder) {
        super(repo, UserDTO.class, User.class, mapService);
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    @Override
    public void save(UserDTO user) {
        if (user.getPassword() != null)
            user.setPassword(encoder.encode(user.getPassword()));
        super.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> user = ((IUserRepo) repository).findByUsernameContains(username);
        if (user.isEmpty()) {
            return null;
        }
        return user.stream().map(value -> new org.springframework.security.core.userdetails.User(value.getUsername(),
                value.getPassword(), value.getRoles().stream().map(SimpleGrantedAuthority::new).toList())).toList().get(0);
    }

    @Override
    public JwtResponse authenticate(JwtUser jwtUser) {
        List<User> user = ((IUserRepo) repository).findByUsernameContains(jwtUser.getUsername());
        authenticateManager(user.get(0).getUsername(), jwtUser.getPassword());
        return new JwtResponse(jwtTokenUtil.generateToken(toDTO(user.get(0))));
    }


    private void authenticateManager(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new RuntimeException("Invalid Credentials");
        }
    }

    @Override
    public UserDTO toDTO(User entity) {
        UserDTO dto = super.toDTO(entity);
        dto.setPassword(null);
        return dto;
    }
}
