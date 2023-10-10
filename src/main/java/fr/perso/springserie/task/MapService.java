package fr.perso.springserie.task;

import fr.perso.springserie.model.dto.*;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.*;
import fr.perso.springserie.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapService {

    private final Map<String, IBaseService<? extends BaseDTO>> mapService;
    private final Map<String, Class<?>> mapClass;
    private final Map<String, IBaseRepo<? extends BaseEntity>> mapRepo;


    @Autowired
    @Lazy
    public MapService(IActorService actorService, ICharacterService characterService, ISeriesService seriesService, ICategoryService categoryService,
                      ISeasonService seasonService, IEpisodeService episodeService, IMovieService movieService,
                      IUserService userService, ISeriesRepo seriesRepo, ISeasonRepo seasonRepo, IEpisodeRepo episodeRepo,
                      IActorRepo actorRepo, ICharacterRepo characterRepo, IMovieRepo movieRepo, ICategoryRepo categoryRepo) {
        mapService = new HashMap<>();

        mapService.put("actor", actorService);
        mapService.put("character", characterService);
        mapService.put("series", seriesService);
        mapService.put("season", seasonService);
        mapService.put("category", categoryService);
        mapService.put("episode", episodeService);
        mapService.put("movie", movieService);
        mapService.put("user", userService);


        mapClass = new HashMap<>();
        mapClass.put("actor", ActorDTO.class);
        mapClass.put("character", CharacterDTO.class);
        mapClass.put("series", SeriesDTO.class);
        mapClass.put("season", SeasonDTO.class);
        mapClass.put("category", CategoryDTO.class);
        mapClass.put("episode", EpisodeDTO.class);
        mapClass.put("movie", MovieDTO.class);
        mapClass.put("user", UserDTO.class);


        mapRepo = new HashMap<>();
        mapRepo.put("actor", actorRepo);
        mapRepo.put("character", characterRepo);
        mapRepo.put("series", seriesRepo);
        mapRepo.put("season", seasonRepo);
        mapRepo.put("category", categoryRepo);
        mapRepo.put("episode", episodeRepo);
        mapRepo.put("movie", movieRepo);
    }

    public IBaseService<BaseDTO> getService(String key) {
        return (IBaseService<BaseDTO>) mapService.get(key);
    }

    public Class<?> getClass(String key) {
        return mapClass.get(key);
    }

    public IBaseRepo<BaseEntity> getRepo(String key) {
        return (IBaseRepo<BaseEntity>) mapRepo.get(key);
    }
}
