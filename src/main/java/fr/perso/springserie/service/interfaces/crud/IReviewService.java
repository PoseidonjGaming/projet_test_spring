package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.review.NamedReviewDTO;
import fr.perso.springserie.model.dto.review.ReviewDTO;

import java.util.List;

public interface IReviewService extends IBaseService<ReviewDTO>{
    List<NamedReviewDTO> getNamedReview(List<Integer> ids);
}
