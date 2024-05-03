package fr.perso.springserie.utility;

import fr.perso.springserie.interceptor.exception.GenericException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
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


    public static <O> O get(Field field, Object object) {
        O returned;
        try {
            field.setAccessible(true);
            returned = (O) field.get(object);
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new GenericException(e);
        }
        return returned;

    }

    public static <O, T> void set(O source, T target, Field targetField) {
        try {
            if (targetField != null) {
                targetField.setAccessible(true);
                targetField.set(target, source);
                targetField.setAccessible(false);
            }

        } catch (IllegalAccessException e) {
            throw new GenericException(e);
        }
    }

    public static <O> Field getField(String name, Class<O> sourceClass) {
        if (sourceClass != null) {
            try {
                return sourceClass.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                return null;
            }
        }
        return null;


    }

    public static Map<String, String> getMap(Class<?> clazz, BiConsumer<Field, Map<String, String>> consumer) {
        Map<String, String> map = new LinkedHashMap<>();
        browseField(clazz, field -> consumer.accept(field, map));
        return map;
    }

}
