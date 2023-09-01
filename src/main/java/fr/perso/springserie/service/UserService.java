package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.repository.IUserRepo;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.security.JwtUtil;
import fr.perso.springserie.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService extends BaseService<User, UserDTO> implements IUserService, UserDetailsService {

    @Lazy
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    protected JwtUtil jwtTokenUtil;

    @Autowired
    @Lazy
    protected AuthenticationManager authenticationManager;

    public UserService(IUserRepo repo) {
        super(repo, UserDTO.class, User.class);
    }

    @Override
    public void save(UserDTO user) {
        if (user.getPassword() != null)
            user.setPassword(encoder.encode(user.getPassword()));
        super.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = ((IUserRepo) repository).findByUsername(username);
        return user.map(value -> new org.springframework.security.core.userdetails.User(value.getUsername(), value.getPassword(),
                value.getRoles().stream().map(SimpleGrantedAuthority::new).toList())).orElse(null);
    }

    @Override
    public JwtResponse authenticate(JwtUser jwtUser) {
        UserDTO user = toDTO(((IUserRepo) repository).findByUsername(jwtUser.getUsername()).orElse(null));
        authenticateManager(user.getUsername(), jwtUser.getPassword());
        return new JwtResponse(jwtTokenUtil.generateToken(user));
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
}
