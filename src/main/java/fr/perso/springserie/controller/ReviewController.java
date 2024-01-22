package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.review.NamedReviewDTO;
import fr.perso.springserie.model.dto.review.ReviewDTO;
import fr.perso.springserie.service.interfaces.crud.IReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController extends BaseController<ReviewDTO, IReviewService> {
    protected ReviewController(IReviewService service) {
        super(service);
    }

    @PostMapping("/named")
    public ResponseEntity<List<NamedReviewDTO>> getNamed(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(service.getNamedReview(ids));
    }
}
