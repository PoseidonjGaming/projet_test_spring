package fr.perso.springserie.service.utility;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import jakarta.persistence.Embedded;
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


    public static <D extends BaseDTO> ExampleMatcher.MatchMode getMode(SearchDTO<D> searchDto, Class<D> dtoClass) {
        boolean typeSearch = Arrays.stream(dtoClass.getDeclaredFields()).filter(field ->
                        field.getType().equals(List.class) ||
                                field.getName().endsWith("Ids") ||
                                field.getName().endsWith("Id"))
                .allMatch(field -> get(field, searchDto.getDto()) == null);
        if (typeSearch) {
            return searchDto.getMode();
        }
        return ExampleMatcher.MatchMode.ANY;
    }

    static <O> void findInEmbedded(Class<O> clazz, String searchedField, List<String> pathToField) {
        Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Embedded.class))
                .forEach(embeddedField -> {
                    List<String> field = findField(embeddedField.getType(), searchedField);
                    if (!field.isEmpty()) {
                        pathToField.add(embeddedField.getName());
                        pathToField.addAll(field);
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
        findInEmbedded(clazz, searchedField, pathToField);

        Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.isAnnotationPresent(Embedded.class))
                .forEach(field -> {
                    if (field.getName().equals(searchedField))
                        pathToField.add(field.getName());
                });
        if (!clazz.equals(Object.class)) {
            findInSuperClass(clazz, searchedField, pathToField);
        }

        return pathToField;
    }

    static <O> void findInSuperClass(Class<O> clazz, String searchedField, List<String> pathToField) {
        browseField(clazz.getSuperclass(), field -> {
            if (!field.getType().isPrimitive()) {
                pathToField.addAll(findField(field.getType(), searchedField));

            } else {
                if (pathToField.isEmpty())
                    pathToField.add(field.getName());
            }

        });
    }

    public static <D extends BaseDTO> boolean filtering(D dto, SearchDTO<D> searchDto) {
        final boolean[] filtered = {true};
        browseField(dto.getClass(), field -> {
            if (field.getName().endsWith("Id")) {
                filterId(dto, searchDto, field, filtered);
            }

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

    private static <D extends BaseDTO> void filterId(D dto, SearchDTO<D> searchDto, Field field, boolean[] filtered) {
        Integer id = get(field, dto);
        Integer searchedId = get(field, searchDto.getDto());
        if (searchDto.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
            filtered[0] = filtered[0] && equalsId(id, searchedId);
        } else {
            filtered[0] = filtered[0] || equalsId(id, searchedId);
        }
    }

    public static boolean equalsId(Integer entityId, Integer searchedId) {
        if (entityId != null && searchedId != null)
            return entityId.equals(searchedId);
        return true;
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
