package fr.perso.springserie.service.imp.crud;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.repository.ISeasonRepo;
import fr.perso.springserie.service.interfaces.crud.ISeasonService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import org.springframework.stereotype.Service;

@Service
public class SeasonService extends BaseService<Season, SeasonDTO> implements ISeasonService {
    protected SeasonService(ISeasonRepo repository, IMapper mapper, MapService mapService) {
        super(repository, mapper, SeasonDTO.class, Season.class, mapService);
    }
}
