package fr.perso.springserie.service.interfaces.crud;

import fr.perso.springserie.model.dto.ReviewDTO;

import java.util.List;

public interface IReviewService extends IBaseService<ReviewDTO> {
    List<ReviewDTO> getByUsername(String username);
}
