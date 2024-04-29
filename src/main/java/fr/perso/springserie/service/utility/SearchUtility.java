package fr.perso.springserie.service.utility;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.utility.annotation.Entity;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.ExampleMatcher;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static fr.perso.springserie.service.utility.ServiceUtility.browseField;
import static fr.perso.springserie.service.utility.ServiceUtility.get;

@UtilityClass
public class SearchUtility {

    public static <E extends BaseEntity> ExampleMatcher getMatcher(ExampleMatcher.MatchMode mode,
                                                                   ExampleMatcher.StringMatcher matcherType,
                                                                   Class<E> entityClass) {
        final ExampleMatcher[] exampleMatcher = new ExampleMatcher[1];
        if (mode.equals(ExampleMatcher.MatchMode.ALL)) {
            exampleMatcher[0] = ExampleMatcher.matchingAll();
        } else {
            exampleMatcher[0] = ExampleMatcher.matchingAny();
        }

        exampleMatcher[0] = exampleMatcher[0].withIgnoreNullValues().withIgnorePaths("id");

        getSpecifiers(exampleMatcher, entityClass, entityClass, matcherType);

        return exampleMatcher[0];
    }

    public static ExampleMatcher getUserMatcher() {
        return ExampleMatcher.matchingAll()
                .withIgnorePaths("roles", "password", "id")
                .withIgnoreNullValues().withMatcher("username", matcher -> matcher.exact().caseSensitive());
    }


    public static <E extends BaseEntity> void getSpecifiers(ExampleMatcher[] matcher,
                                                            Class<?> clazz, Class<E> entityClass,
                                                            ExampleMatcher.StringMatcher stringMatcher) {
        browseField(clazz, field -> {
            if (field.getType().isAnnotationPresent(Entity.class) || field.getType().equals(List.class)) {
                List<String> pathField = findField(entityClass, field.getName());
                matcher[0] = matcher[0].withIgnorePaths(getPath(pathField.toArray(new String[]{})));
            } else if (field.getType().equals(String.class)) {
                matcher[0] = matcher[0].withMatcher(
                        getPath(findField(entityClass, field.getName()).toArray(new String[]{})), matcher1 -> matcher1.stringMatcher(stringMatcher).ignoreCase());
            }
        });
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
        final boolean[] filtered = {true};
        browseField(dto.getClass(), field -> {
            if (field.getName().endsWith("Ids") && field.getType().equals(List.class)) {
                filterList(dto, searchDto, field, filtered);
            }

            if (field.getType().equals(LocalDate.class)) {
                filterDate(dto, searchDto, field, filtered);
            }
        });

        return filtered[0];
    }

    private static <D extends BaseDTO> void filterDate(D dto, SearchDTO<D> searchDto, Field field, boolean[] filtered) {
        LocalDate entityDate = get(field, dto);
        if (searchDto.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
            filtered[0] = filtered[0] && isBetween(entityDate, searchDto.getStartDate(), searchDto.getEndDate());
        } else {
            filtered[0] = filtered[0] || isBetween(entityDate, searchDto.getStartDate(), searchDto.getEndDate());
        }
    }

    private static <D extends BaseDTO> void filterList(D dto, SearchDTO<D> searchDto, Field field, boolean[] filtered) {
        List<Integer> ids = get(field, dto);
        List<Integer> searchIds = get(field, searchDto.getDto());
        if (searchDto.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
            filtered[0] = filtered[0] && contains(ids, searchIds);
        } else {
            filtered[0] = filtered[0] || contains(ids, searchIds);
        }
    }


    public static <O> boolean contains(List<O> entityList, List<O> compareTo) {
        if ((entityList != null) && (compareTo != null)) {
            return new HashSet<>(entityList).containsAll(compareTo);
        }
        return true;

    }

    public static boolean isBetween(LocalDate releaseDate, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return (releaseDate.isEqual(startDate) || releaseDate.isEqual(endDate))
                    || (releaseDate.isAfter(startDate) && releaseDate.isBefore(endDate));
        }
        return true;

    }
}
