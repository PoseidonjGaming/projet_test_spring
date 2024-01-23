package fr.perso.springserie.task;

import fr.perso.springserie.model.dto.*;
import fr.perso.springserie.model.dto.review.ReviewDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.*;
import fr.perso.springserie.service.interfaces.crud.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapService {

    private static final String ACTOR = "actor";
    private static final String CHARACTER = "character";
    private static final String SERIES = "series";
    private static final String SEASON = "season";
    private static final String CATEGORY = "category";
    private static final String EPISODE = "episode";
    private static final String MOVIE = "movie";
    private static final String USER = "user";
    private static final String REVIEW = "review";
    private final Map<String, IBaseService<? extends BaseDTO>> mapService;
    private final Map<String, Class<? extends BaseDTO>> mapClass;
    private final Map<String, IBaseRepo<? extends BaseEntity>> mapRepo;

    @Autowired
    @Lazy
    public MapService(IActorService actorService, ICharacterService characterService,
                      ISeriesService seriesService, ICategoryService categoryService,
                      ISeasonService seasonService, IEpisodeService episodeService,
                      IMovieService movieService, IUserService userService,
                      IReviewService reviewService, ISeriesRepo seriesRepo,
                      ISeasonRepo seasonRepo, IEpisodeRepo episodeRepo,
                      IActorRepo actorRepo, ICharacterRepo characterRepo,
                      IMovieRepo movieRepo, ICategoryRepo categoryRepo,
                      IUserRepo userRepo, IReviewRepo reviewRepo) {
        mapService = new HashMap<>();

        mapService.put(ACTOR, actorService);
        mapService.put(CHARACTER, characterService);
        mapService.put(SERIES, seriesService);
        mapService.put(SEASON, seasonService);
        mapService.put(CATEGORY, categoryService);
        mapService.put(EPISODE, episodeService);
        mapService.put(MOVIE, movieService);
        mapService.put(USER, userService);
        mapService.put(REVIEW, reviewService);


        mapClass = new HashMap<>();
        mapClass.put(ACTOR, ActorDTO.class);
        mapClass.put(CHARACTER, CharacterDTO.class);
        mapClass.put(SERIES, SeriesDTO.class);
        mapClass.put(SEASON, SeasonDTO.class);
        mapClass.put(CATEGORY, CategoryDTO.class);
        mapClass.put(EPISODE, EpisodeDTO.class);
        mapClass.put(MOVIE, MovieDTO.class);
        mapClass.put(USER, UserDTO.class);
        mapClass.put(REVIEW, ReviewDTO.class);


        mapRepo = new HashMap<>();
        mapRepo.put(ACTOR, actorRepo);
        mapRepo.put(CHARACTER, characterRepo);
        mapRepo.put(SERIES, seriesRepo);
        mapRepo.put(SEASON, seasonRepo);
        mapRepo.put(CATEGORY, categoryRepo);
        mapRepo.put(EPISODE, episodeRepo);
        mapRepo.put(MOVIE, movieRepo);
        mapRepo.put(USER, userRepo);
        mapRepo.put(REVIEW, reviewRepo);
    }

    public IBaseService<BaseDTO> getService(String key) {
        return (IBaseService<BaseDTO>) mapService.get(key);
    }

    public Class<? extends BaseDTO> getClass(String key) {
        return mapClass.get(key);
    }

    public IBaseRepo<BaseEntity> getRepo(String key) {
        return (IBaseRepo<BaseEntity>) mapRepo.get(key);
    }
}
