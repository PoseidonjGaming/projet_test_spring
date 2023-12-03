package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.CategoryDTO;
import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.service.interfaces.ICategoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController<CategoryDTO> {
    public CategoryController(ICategoryService service) {
        super(service);
    }
}
