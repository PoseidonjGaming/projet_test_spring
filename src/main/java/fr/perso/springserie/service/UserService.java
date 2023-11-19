package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.repository.IUserRepo;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.security.JwtUtil;
import fr.perso.springserie.service.interfaces.IUserService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.service.mapper.UserMapper;
import fr.perso.springserie.task.MapService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UserService extends BaseService<User, UserDTO> implements IUserService, UserDetailsService {

    protected final JwtUtil jwtTokenUtil;
    protected final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    @Lazy
    public UserService(IUserRepo repo, MapService mapService, JwtUtil jwtTokenUtil,
                       AuthenticationManager authenticationManager, PasswordEncoder encoder, UserMapper customMapper) {
        super(repo, UserDTO.class, User.class, mapService, customMapper);
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
                value.getPassword(), getRoles(value.getRoles()).stream().map(SimpleGrantedAuthority::new).toList())).toList().get(0);
    }

    private List<String> getRoles(String roles) {
        return Arrays.stream(roles.split(",")).map(e -> e.replace("[", "")
                .replace("\"", "").replace("]", "")).toList();
    }

    @Override
    public JwtResponse authenticate(JwtUser jwtUser) {
        UserDTO userDTO = customMapper.convert(jwtUser, dtoClass);
        userDTO.setPassword(null);
        List<UserDTO> user = search(userDTO, ExampleMatcher.MatchMode.ALL, ExampleMatcher.StringMatcher.EXACT);
        authenticateManager(user.get(0).getUsername(), jwtUser.getPassword());
        return new JwtResponse(jwtTokenUtil.generateToken(user.get(0)));
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
    protected @NotNull ExampleMatcher getMatcher(UserDTO dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType) {
        return ExampleMatcher.matchingAll().withIgnorePaths("password").withIgnoreNullValues().withMatcher("username", matcher -> matcher.exact().caseSensitive());
    }

    @Override
    public void saves(List<UserDTO> userDTOS) {
        super.saves(userDTOS.stream().peek(userDTO -> userDTO.setPassword(encoder.encode(userDTO.getPassword()))).toList());
    }
}
