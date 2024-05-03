package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.ReviewDTO;
import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.model.entity.Review;
import fr.perso.springserie.model.entity.User;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.repository.IUserRepository;
import fr.perso.springserie.service.interfaces.crud.IReviewService;
import fr.perso.springserie.service.mapper.IMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.perso.springserie.utility.SearchUtility.getMatcher;
import static fr.perso.springserie.utility.SearchUtility.getUserMatcher;

@Service
public class ReviewService extends BaseService<Review, ReviewDTO> implements IReviewService {
    private final IUserRepository userRepository;

    protected ReviewService(IBaseRepository<Review> repository, IMapper mapper, IUserRepository userRepository) {
        super(repository, mapper, ReviewDTO.class, Review.class);
        this.userRepository = userRepository;
    }

    @Override
    public List<ReviewDTO> getByUsername(String username) {
        User user = userRepository.findOne(
                Example.of(mapper.convert(new UserDTO(username), User.class),
                        getUserMatcher())).orElse(null);
        assert user != null;
        return mapper.convertList(repository.findAll(
                Example.of(new Review(null, null, user.getId(), null, null),
                        getMatcher(ExampleMatcher.MatchMode.ANY, ExampleMatcher.StringMatcher.EXACT, entityClass))
        ), dtoClass);
    }
}
