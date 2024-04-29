package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.service.MapService;
import org.springframework.stereotype.Service;

@Service
public class SeasonService extends BaseService<Season, SeasonDTO> {
    protected SeasonService(IBaseRepository<Season> repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, SeasonDTO.class, Season.class, mapService);
    }
}
