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
}