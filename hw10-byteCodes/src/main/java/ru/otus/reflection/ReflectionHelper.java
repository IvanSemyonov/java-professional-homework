package ru.otus.reflection;

import java.lang.reflect.Method;
import java.util.Arrays;


public class ReflectionHelper {

    public static <T> T instantiate(Class<T> type, Object... args) {
        try {
            if (args.length == 0) {
                return type.getDeclaredConstructor().newInstance();
            } else {
                Class<?>[] classes = toClasses(args);
                return type.getDeclaredConstructor(classes).newInstance(args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?>[] toClasses(Object[] args) {
        return Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
    }

    public static boolean equalMethods(Method method1, Method method2){
        return method1.getName().equals(method2.getName())
                && method1.getReturnType().equals(method2.getReturnType())
                && equalParamTypes(method1.getParameterTypes(), method2.getParameterTypes());
    }

    public static boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == params2.length) {
            for(int i = 0; i < params1.length; ++i) {
                if (params1[i] != params2[i]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
