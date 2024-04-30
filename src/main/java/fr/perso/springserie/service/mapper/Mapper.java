package fr.perso.springserie.service.mapper;

import fr.perso.springserie.interceptor.exception.GenericException;
import fr.perso.springserie.model.entity.Character;
import fr.perso.springserie.model.entity.*;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.utility.annotation.Entity;
import fr.perso.springserie.utility.annotation.Json;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.perso.springserie.service.utility.ServiceUtility.*;

@Service
@Primary
public class Mapper implements IMapper {

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


    public Mapper(IBaseRepository<Series> seriesRepo, IBaseRepository<Category> categoryRepo,
                  IBaseRepository<Character> characterRepo, IBaseRepository<Actor> actorRepository,
                  IBaseRepository<Season> seasonRepository, IBaseRepository<Episode> episodeRepository,
                  IBaseRepository<Movie> movieRepository, IBaseRepository<User> userRepository,
                  IBaseRepository<Review> reviewRepository) {
        mapRepo = new HashMap<>();
        mapRepo.put(SERIES, seriesRepo);
        mapRepo.put(CATEGORY, categoryRepo);
        mapRepo.put(CHARACTER, characterRepo);
        mapRepo.put(ACTOR, actorRepository);
        mapRepo.put(SEASON, seasonRepository);
        mapRepo.put(EPISODE, episodeRepository);
        mapRepo.put(MOVIE, movieRepository);
        mapRepo.put(USER, userRepository);
        mapRepo.put(REVIEW, reviewRepository);
    }

    @Override
    public <S, T> T convert(S source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            browseField(source.getClass(), target, (field, object) -> map(source.getClass(), source, object, field));
            return target;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new GenericException(e);
        }
    }


    private <S, T> void map(Class<?> sourceClass, S source, T target, Field sourceField) {
        if (sourceField.getType().isAnnotationPresent(Entity.class)) {
            mapEntityId(source, target, sourceField);
        } else if (sourceField.getType().equals(List.class)) {
            mapList(source, target, sourceField);
        } else {
            transfert(source, target, sourceField, target.getClass());
        }

        Class<?> superClass = sourceClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            browseField(superClass, target, (superField, o) -> map(superClass, source, o, superField));
        }
    }

    private <S, T> void mapEntityId(S source, T target, Field sourceField) {
        Field targetField = getField(sourceField.getName().concat("Id"), target.getClass());
        if (targetField != null) {
            BaseEntity entity = get(sourceField, source);
            if (entity != null)
                set(entity.getId(), target, targetField);
        }
    }

    private <S, T> void mapList(S source, T target, Field sourceField) {
        Class<?> listType = (Class<?>) ((ParameterizedType) sourceField.getGenericType()).getActualTypeArguments()[0];
        Field targetField = getField(sourceField.getName().concat("Ids"), target.getClass());
        if (Objects.isNull(targetField)) {
            targetField = getField(sourceField.getName(), target.getClass());
        }

        if (listType.isAnnotationPresent(Entity.class)) {
            List<BaseEntity> entities = get(sourceField, source);
            if (entities != null)
                set(entities.stream().map(BaseEntity::getId).toList(), target, targetField);

        } else if(sourceField.isAnnotationPresent(Json.class)){
            set(mapRepo.get(sourceField.getAnnotation(Json.class).type()).findByIdIn(get(sourceField, source)), target, targetField);
        }else{
            transfert(source, target, sourceField, target.getClass());
        }

    }


    private <S, T> void transfert(S source, T target, Field sourceField, Class<?> targetClass) {
        if (source.getClass().getSuperclass().equals(sourceField.getDeclaringClass())
                || source.getClass().equals(sourceField.getDeclaringClass())) {
            Field targetField = getField(sourceField.getName(), targetClass);
            if (targetField != null) {
                set(get(sourceField, source), target, targetField);
            } else if (!targetClass.getSuperclass().equals(Object.class)) {
                transfert(source, target, sourceField, targetClass.getSuperclass());
            }
        }

    }
}
