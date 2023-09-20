package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.CategoryDTO;
import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.repository.ICategoryRepo;
import fr.perso.springserie.service.interfaces.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends BaseService<Category, CategoryDTO> implements ICategoryService {
    protected CategoryService(ICategoryRepo repository) {
        super(repository, CategoryDTO.class, Category.class);
    }

    @Override
    public List<CategoryDTO> search(String term) {
        return null;
    }
}
