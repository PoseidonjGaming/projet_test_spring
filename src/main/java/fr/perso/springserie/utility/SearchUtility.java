package fr.perso.springserie.utility;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.utility.annotation.Entity;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.ExampleMatcher;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static fr.perso.springserie.utility.ServiceUtility.*;

@UtilityClass
public class SearchUtility {

    public static <E extends BaseEntity, D extends BaseDTO>
    ExampleMatcher getMatcher(SearchDTO<D> searchDTO, Class<E> entityClass) {
        boolean isNull = Arrays.stream(searchDTO.getDto().getClass().getDeclaredFields())
                .allMatch(field -> Objects.isNull(get(field, searchDTO.getDto())));
        ExampleMatcher matcher;
        if (isNull) {
            matcher = ExampleMatcher
                    .matchingAll()
            ;
        } else {
            matcher = ExampleMatcher
                    .matchingAny();
        }

        return getSpecifiers(matcher.withIgnoreNullValues()
                .withIgnorePaths("id"), entityClass, searchDTO.getType());
    }

    public static ExampleMatcher getUserMatcher() {
        return ExampleMatcher.matchingAll()
                .withIgnorePaths("password", "id", "review", "roles", "seriesWatchlist", "moviesWatchlist")
                .withIgnoreNullValues().withMatcher("username", matcher -> matcher.exact().caseSensitive());
    }


    public static ExampleMatcher getSpecifiers(ExampleMatcher initMatcher,
                                               Class<?> clazz, ExampleMatcher.StringMatcher stringMatcher) {
        List<ExampleMatcher> matchers = new ArrayList<>();
        browseField(clazz, initMatcher, (field, matcher) -> {
            if (field.getType().equals(List.class) || field.getType().equals(LocalDate.class)) {
                matcher.getIgnoredPaths().add(field.getName());
            }

            if (field.getType().equals(String.class)) {
                matchers.add(matcher.withMatcher(field.getName(), match -> match.stringMatcher(stringMatcher).ignoreCase()));
            }

        });

        return matchers.stream().reduce((exampleMatcher, exampleMatcher2) -> {
            exampleMatcher2.getPropertySpecifiers().getSpecifiers().forEach(value -> {
                exampleMatcher.getPropertySpecifiers().add(value);
            });
            return exampleMatcher;
        }).orElse(initMatcher);
    }

    public static String getPath(String... parts) {
        if (parts[0].isEmpty()) {
            return Arrays.stream(parts).skip(1).reduce((s, s2) -> s + "." + s2).orElse("");
        }
        return Arrays.stream(parts).reduce((s, s2) -> s + "." + s2).orElse("");
    }

    public static <O> List<String> findField(Class<O> clazz, String searchedField) {
        List<String> pathToField = new ArrayList<>();

        Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.getName().equals(searchedField)).findFirst()
                .ifPresent(field -> pathToField.add(field.getName()));

        if (pathToField.isEmpty()) {
            Class<?> superClass = clazz.getSuperclass();
            while (!superClass.equals(Object.class)) {
                pathToField.addAll(findField(superClass, searchedField));
                superClass = superClass.getSuperclass();
            }
        }

        if (pathToField.isEmpty()) {
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.getType().isAnnotationPresent(Entity.class))
                    .forEach(field -> {
                        List<String> path = findField(field.getType(), searchedField);
                        if (!path.isEmpty() && pathToField.isEmpty()) {
                            pathToField.add(field.getName());
                            pathToField.addAll(path);
                        }
                    });
        }

        return pathToField;
    }

    public static <D extends BaseDTO> boolean filtering(D dto, SearchDTO<D> searchDto) {
        Predicate<Field> predicate = field -> {
            if (field.getType().equals(List.class)) {
                return contains(get(field, dto), get(field, searchDto.getDto()));
            }

            return Objects.nonNull(get(field, dto));
        };

        if (searchDto.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
            return Arrays.stream(dto.getClass().getDeclaredFields())
                    .allMatch(predicate)
                    && searchDto.getDates().stream().allMatch(dateDTO ->
                    isBetween(get(getField(dateDTO.getField(), dto.getClass()), dto),
                            dateDTO.getStartDate(), dateDTO.getEndDate()));
        } else {
            return Arrays.stream(dto.getClass().getDeclaredFields())
                    .anyMatch(predicate) || searchDto.getDates().stream().anyMatch(dateDTO ->
                    isBetween(get(getField(dateDTO.getField(), dto.getClass()), dto),
                            dateDTO.getStartDate(), dateDTO.getEndDate()));
        }

    }

    public static <O> boolean contains(List<O> entityList, List<O> compareTo) {
        if (Objects.nonNull(entityList) && Objects.nonNull(compareTo)) {
            return new HashSet<>(entityList).containsAll(compareTo);
        } else if (Objects.nonNull(compareTo)) {
            return compareTo.isEmpty();
        } else {
            return true;
        }
    }

    public static boolean isBetween(LocalDate releaseDate, LocalDate startDate, LocalDate endDate) {
        if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
            return (releaseDate.isEqual(startDate) || releaseDate.isEqual(endDate))
                    || (releaseDate.isAfter(startDate) && releaseDate.isBefore(endDate));
        }
        return Objects.nonNull(releaseDate);

    }
}
