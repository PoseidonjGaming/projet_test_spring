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
    public PagedResponse<UserDTO> getAll(int size, int page) {
        PagedResponse<UserDTO> userDTOS = super.getAll(size, page);
        userDTOS.getContent().forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public UserDTO getById(int id) {
        UserDTO userDTO = super.getById(id);
        userDTO.setPassword("");
        return userDTO;
    }

    @Override
    public PagedResponse<UserDTO> getByIds(List<Integer> ids, int size, int page) {
        PagedResponse<UserDTO> userDTOS = super.getByIds(ids, size, page);
        userDTOS.getContent().forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public List<UserDTO> getByIds(List<Integer> ids) {
        List<UserDTO> userDTOS = super.getByIds(ids);
        userDTOS.forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public List<UserDTO> search(SearchDTO<UserDTO> searchDto) {
        List<UserDTO> userDTOS = super.search(searchDto);
        userDTOS.forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public PagedResponse<UserDTO> search(SearchDTO<UserDTO> searchDto, int size, int page) {
        PagedResponse<UserDTO> userDTOS = super.search(searchDto, size, page);
        userDTOS.getContent().forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public PagedResponse<UserDTO> sort(SortDTO sortDTO, int size, int page) {
        PagedResponse<UserDTO> userDTOS = super.sort(sortDTO, size, page);
        userDTOS.getContent().forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public List<UserDTO> sort(SortDTO sortDTO) {
        List<UserDTO> userDTOS = super.sort(sortDTO);
        userDTOS.forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }


    @Override
    public PagedResponse<UserDTO> sortSearch(SearchDTO<UserDTO> searchDto, SortDTO sortDTO, int size, int page) {
        PagedResponse<UserDTO> userDTOS = super.sortSearch(searchDto, sortDTO, size, page);
        userDTOS.getContent().forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public List<UserDTO> sortSearch(SearchDTO<UserDTO> searchDto, SortDTO sortDTO) {
        List<UserDTO> userDTOS = super.sortSearch(searchDto, sortDTO);
        userDTOS.forEach(userDTO -> userDTO.setPassword(""));
        return userDTOS;
    }

    @Override
    public UserDTO save(UserDTO dto) {
        if (dto.getPassword() != null) {
            dto.setPassword(encoder.encode(dto.getPassword()));
        }
        return super.save(dto);
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
