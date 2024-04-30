package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.ReviewDTO;
import fr.perso.springserie.service.interfaces.crud.IReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController extends BaseController<ReviewDTO, IReviewService> {
    protected ReviewController(IReviewService service) {
        super(service);
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<List<ReviewDTO>> getByUsername(@PathVariable String username){
        return ResponseEntity.ofNullable(service.getByUsername(username));
    }
}
