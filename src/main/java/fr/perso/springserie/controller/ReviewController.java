package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.review.ReviewDTO;
import fr.perso.springserie.service.interfaces.crud.IReviewService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
public class ReviewController extends BaseController<ReviewDTO, IReviewService> {
    protected ReviewController(IReviewService service) {
        super(service);
    }


}
