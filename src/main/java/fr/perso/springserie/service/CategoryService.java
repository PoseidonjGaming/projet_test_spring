package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.CategoryDTO;
import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.repository.ICategoryRepo;
import fr.perso.springserie.service.interfaces.ICategoryService;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends BaseService<Category, CategoryDTO> implements ICategoryService {
    protected CategoryService(ICategoryRepo repository, MapService mapService) {
        super(repository, CategoryDTO.class, Category.class, mapService);
    }
}
