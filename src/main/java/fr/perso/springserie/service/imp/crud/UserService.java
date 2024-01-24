package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public void saves(List<UserDTO> userDTOS) {
        userDTOS.forEach(userDTO -> userDTO.setPassword(encoder.encode(userDTO.getPassword())));
        super.saves(userDTOS);
    }

    @Override
    public UserDTO save(UserDTO dto) {
        if (dto.getPassword() != null)
            dto.setPassword(encoder.encode(dto.getPassword()));
        return super.save(dto);
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
    public void registration(UserDTO userDTO) {
        userDTO.setRoles(List.of("ROLE_user"));
        save(userDTO);
    }

    @Override
    public List<SeriesDTO> addToWatchList(int seriesId, String username) {
        SearchDTO<UserDTO> searchDTO = new SearchDTO<>(
                new UserDTO(username),
                ExampleMatcher.MatchMode.ALL,
                ExampleMatcher.StringMatcher.EXACT,
                null, null);
        List<UserDTO> dtos = search(searchDTO);
        if (dtos.size() == 1) {
            UserDTO userDTO = dtos.get(0);
            if (!userDTO.getSeriesIds().contains(seriesId)) {
                userDTO.getSeriesIds().add(seriesId);
            }
            User user = repository.save(mapper.convert(userDTO, entityClass));
            return mapper.convertList(user.getSeries(), SeriesDTO.class);
        }
        return new ArrayList<>();
    }

    @Override
    public List<SeriesDTO> removeFromWatchList(Integer seriesId, String username) {
        SearchDTO<UserDTO> searchDTO = new SearchDTO<>(
                new UserDTO(username),
                ExampleMatcher.MatchMode.ALL,
                ExampleMatcher.StringMatcher.EXACT,
                null, null);
        List<UserDTO> dtos = search(searchDTO);
        if (dtos.size() == 1) {
            UserDTO userDTO = dtos.get(0);
            if (userDTO.getSeriesIds().contains(seriesId)) {
                userDTO.getSeriesIds().remove(seriesId);
            }
            User user = repository.save(mapper.convert(userDTO, entityClass));
            return mapper.convertList(user.getSeries(), SeriesDTO.class);
        }
        return new ArrayList<>();
    }

    @Override
    public UserDTO searchByUsername(String username) {
        List<UserDTO> users = search(
                new SearchDTO<>(new UserDTO(username),
                        ExampleMatcher.MatchMode.ALL,
                        ExampleMatcher.StringMatcher.EXACT,
                        null, null)
        );

        if(!users.isEmpty()){
            users.get(0).erasePassword();
            return users.get(0);
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
