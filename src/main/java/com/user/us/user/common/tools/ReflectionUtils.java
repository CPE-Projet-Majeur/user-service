package com.user.us.user.common.tools;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionUtils {

    public static Object getPrivateField(Object object, String fieldName) {
        try {
            System.out.println(object.toString());
            System.out.println(object.getClass().toString());

            String test = Arrays.toString(object.getClass().getDeclaredFields());
            System.out.println(test);
            Field field = object.getClass().getDeclaredField(fieldName);

            field.setAccessible(true); // Rendre le champ accessible
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getPrivateFieldRecursively(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // Passe à la classe parente
            } catch (IllegalAccessException e) {
                System.err.println("Impossible d'accéder au champ " + fieldName + ": " + e.getMessage());
                return null;
            }
        }
        System.err.println("Le champ " + fieldName + " n'existe pas dans la hiérarchie de classes.");
        return null;
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}