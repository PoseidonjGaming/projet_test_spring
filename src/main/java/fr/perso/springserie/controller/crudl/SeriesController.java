package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.service.mapper.IMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController extends BaseController<SeriesDTO, IBaseService<SeriesDTO>> {
    private final IBaseRepository<Series> repo;
    private final IMapper mapper;

    protected SeriesController(IBaseService<SeriesDTO> service, IBaseRepository<Series> repo, IMapper mapper) {
        super(service);
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping("/test")
    public ResponseEntity<List<SeriesDTO>> test() {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withMatcher("name", matcher1 ->
                matcher1.stringMatcher(ExampleMatcher.StringMatcher.CONTAINING).ignoreCase());
        SeriesDTO seriesDTO = new SeriesDTO();
        seriesDTO.setName("l");
        Example<Series> example = Example.of(mapper.convert(seriesDTO, Series.class), matcher);
        return ResponseEntity.ok(mapper.convertList(repo.findAll(example), SeriesDTO.class));
    }
}
