package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.CategoryDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController<CategoryDTO, IBaseService<CategoryDTO>>{
    protected CategoryController(IBaseService<CategoryDTO> service) {
        super(service);
    }
}
