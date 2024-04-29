package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.CategoryDTO;
import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.service.MapService;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends BaseService<Category, CategoryDTO> {
    protected CategoryService(IBaseRepository<Category> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, CategoryDTO.class, Category.class, mapService);
    }
}
