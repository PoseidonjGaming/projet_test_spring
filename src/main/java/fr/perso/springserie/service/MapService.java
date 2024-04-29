package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.model.entity.*;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
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
    private final Map<String, IBaseRepository<? extends BaseEntity>> mapRepo;

    @Autowired
    @Lazy
    public MapService(IBaseRepository<Series> seriesRepo, IBaseRepository<Category> categoryRepo,
                      IBaseRepository<Character> characterRepo, IBaseRepository<Actor> actorRepository,
                      IBaseRepository<Season> seasonRepository, IBaseRepository<Episode> episodeRepository,
                      IBaseRepository<Movie> movieRepository, IBaseRepository<User> userRepository) {

        mapRepo = new HashMap<>();
        mapRepo.put(SERIES, seriesRepo);
        mapRepo.put(CATEGORY, categoryRepo);
        mapRepo.put(CHARACTER, characterRepo);
        mapRepo.put(ACTOR, actorRepository);
        mapRepo.put(SEASON, seasonRepository);
        mapRepo.put(EPISODE, episodeRepository);

    }

    public IBaseRepository<? extends BaseEntity> getRepo(String key) {
        return mapRepo.get(key);
    }
}
