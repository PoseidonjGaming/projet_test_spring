package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.CategoryDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.Category;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.crud.ICategoryService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class CategoryService extends BaseService<Category, CategoryDTO> implements ICategoryService {
    protected CategoryService(IBaseRepo<Category> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, CategoryDTO.class, Category.class, mapService);
    }

    @Override
    protected Predicate<CategoryDTO> predicate(SearchDTO<CategoryDTO> searchDTO) {
        return categoryDTO -> {
            if(searchDTO.getMode().equals(ExampleMatcher.MatchMode.ALL))
                return filterList(categoryDTO.getMovieIds(), searchDTO.getDto().getMovieIds()) &&
                        filterList(categoryDTO.getSeriesIds(), searchDTO.getDto().getMovieIds());
            else  return filterList(categoryDTO.getMovieIds(), searchDTO.getDto().getMovieIds()) ||
                    filterList(categoryDTO.getSeriesIds(), searchDTO.getDto().getMovieIds());
        };
    }
}
