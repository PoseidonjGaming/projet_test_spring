package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.ReviewDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.Review;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.repository.IReviewRepo;
import fr.perso.springserie.service.interfaces.crud.IReviewService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class ReviewService extends BaseService<Review, ReviewDTO> implements IReviewService {
    protected ReviewService(IReviewRepo repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, ReviewDTO.class, Review.class, mapService);
    }
}
