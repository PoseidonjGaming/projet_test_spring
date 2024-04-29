package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.interceptor.exception.GenericException;
import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.security.JwtResponse;
import fr.perso.springserie.security.JwtUser;
import fr.perso.springserie.security.JwtUtil;
import fr.perso.springserie.security.UserRole;
import fr.perso.springserie.service.interfaces.crud.IUserService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.service.MapService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.perso.springserie.service.utility.SearchUtility.getUserMatcher;

@Service
public class UserService extends BaseService<User, UserDTO> implements IUserService {

    protected final JwtUtil jwtTokenUtil;
    protected final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    @Lazy
    protected UserService(IBaseRepository<User> repository, IMapper mapper, MapService mapService,
                          JwtUtil jwtTokenUtil, AuthenticationManager authenticationManager, PasswordEncoder encoder) {
        super(repository, mapper, UserDTO.class, User.class, mapService);
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    private void authenticateManager(@NotNull String username, @NotNull String password) throws GenericException {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public List<UserDTO> search(SearchDTO<UserDTO> searchDto) {
        return mapper.convertList(repository.findAll(Example.of(
                mapper.convert(searchDto.getDto(), entityClass),
                getUserMatcher())), dtoClass);
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

    @Override
    public JwtResponse authentication(JwtUser user) {
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
        userDTO.setRoles(List.of(UserRole.USER_ROLE.getName()));
        save(userDTO);
    }

    @Override
    public List<Integer> addToWatchList(String type, int id, String username) {
        return List.of();
    }

    @Override
    public List<Integer> removeFromWatchList(String type, String seriesId, String username) {
        return List.of();
    }

    @Override
    public UserDTO searchByUsername(String username) {
        return null;
    }
}
