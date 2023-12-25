package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.PagedResponse;
import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.security.JwtUtil;
import fr.perso.springserie.service.interfaces.crud.IUserService;
import fr.perso.springserie.service.mapper.UserMapper;
import fr.perso.springserie.task.MapService;
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

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Service
public class UserService extends BaseService<User, UserDTO> implements IUserService, UserDetailsService {

    protected final JwtUtil jwtTokenUtil;
    protected final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    @Lazy
    public UserService(IBaseRepo<User> repository, UserMapper mapper, MapService mapService,
                       JwtUtil jwtTokenUtil, AuthenticationManager authenticationManager, PasswordEncoder encoder) {
        super(repository, mapper, UserDTO.class, User.class, mapService);
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
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
    protected ExampleMatcher getMatcher(UserDTO dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType) {
        return ExampleMatcher.matchingAll()
                .withIgnorePaths("roles", "password", "id")
                .withIgnoreNullValues().withMatcher("username", matcher -> matcher.exact().caseSensitive());
    }

    @Override
    protected Predicate<UserDTO> predicate(SearchDTO<UserDTO> searchDTO) {
        return userDTO -> filterList(userDTO.getRoles(), searchDTO.getDto().getRoles());
    }

    @Override
    public void saves(List<UserDTO> userDTOS) {
        userDTOS.forEach(userDTO -> userDTO.setPassword(encoder.encode(userDTO.getPassword())));
        super.saves(userDTOS);
    }

    @Override
    public JwtResponse authenticate(JwtUser user) {
        List<UserDTO> results = search(new SearchDTO<>(mapper.convert(user, UserDTO.class),
                ExampleMatcher.MatchMode.ALL, ExampleMatcher.StringMatcher.EXACT,
                null, null));
        if (!results.isEmpty()) {
            authenticateManager(user.getUsername(), user.getPassword());
            return new JwtResponse(jwtTokenUtil.generateToken(results.get(0)));
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDTO> results = search(new SearchDTO<>(new UserDTO(username),
                ExampleMatcher.MatchMode.ALL, ExampleMatcher.StringMatcher.EXACT,
                null, null));
        if (!results.isEmpty()) {
            UserDTO user = results.get(0);
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    user.getRoles().stream().map(SimpleGrantedAuthority::new).toList());
        }
        return null;
    }
}
