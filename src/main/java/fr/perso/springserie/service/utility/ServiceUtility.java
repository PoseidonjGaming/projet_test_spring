package fr.perso.springserie.service.utility;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import jakarta.persistence.Embedded;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.ExampleMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@UtilityClass
public class ServiceUtility {

    public static <O> void browseField(Class<?> clazz, O object, BiConsumer<Field, O> consumer) {
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> consumer.accept(field, object));
    }

    public static void browseField(Class<?> clazz, Consumer<Field> consumer) {
        Arrays.stream(clazz.getDeclaredFields()).forEach(consumer);
    }

    public static <O> List<String> findField(O object, String searchedField) {
        List<String> pathToField = new ArrayList<>();
        findInEmbedded(object, searchedField, pathToField);

        Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> !field.isAnnotationPresent(Embedded.class))
                .forEach(field -> {
                    if (field.getName().equals(searchedField))
                        pathToField.add(field.getName());
                });
        if (!object.getClass().equals(Object.class)) {
            findInSuperClass(object, searchedField, pathToField);
        }

        return pathToField;
    }

    private static <O> void findInSuperClass(O object, String searchedField, List<String> pathToField) {
        browseField(object.getClass().getSuperclass(), field -> {
            if (!field.getType().isPrimitive()) {
                try {
                    pathToField.addAll(findField(field.getType().getDeclaredConstructor().newInstance(), searchedField));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

            } else {
                if (pathToField.isEmpty())
                    pathToField.add(field.getName());
            }

        });
    }

    private static <O> void findInEmbedded(O object, String searchedField, List<String> pathToField) {
        Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Embedded.class))
                .forEach(embeddedField -> {
                    try {
                        List<String> field = findField(embeddedField.getType().getDeclaredConstructor().newInstance(), searchedField);
                        if (!field.isEmpty()) {
                            pathToField.add(embeddedField.getName());
                            pathToField.addAll(field);
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public static <O> O get(Field field, Object object) {
        O returned;
        try {
            field.setAccessible(true);
            returned = (O) field.get(object);
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return returned;

    }

    public static  <D extends BaseDTO> ExampleMatcher.MatchMode getMode(SearchDTO<D> searchDto, Class<D> dtoClass) {
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
}
