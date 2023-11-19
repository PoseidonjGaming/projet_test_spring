package fr.perso.springserie.service.utility;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ServiceUtility {
    private ServiceUtility(){}
    public static  <O> void browseField(Class<?> clazz, O object, BiConsumer<Field, O> consumer) {
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> consumer.accept(field, object));
    }

    public static void browseField(Class<?> clazz, Consumer<Field> consumer) {
        Arrays.stream(clazz.getDeclaredFields()).forEach(consumer);
    }
}
