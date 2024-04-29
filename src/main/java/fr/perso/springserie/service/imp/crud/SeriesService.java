package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeriesService extends BaseService<Series, SeriesDTO> {
    protected SeriesService(IBaseRepository<Series> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, SeriesDTO.class, Series.class, mapService);
    }
}
