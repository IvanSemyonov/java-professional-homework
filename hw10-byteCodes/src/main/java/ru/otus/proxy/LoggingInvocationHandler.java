package ru.otus.proxy;

import ru.otus.annotations.Log;
import ru.otus.reflection.ReflectionHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class LoggingInvocationHandler<T> implements InvocationHandler {
    private final T myClass;
    private final Set<Method> loggableMethods;

    LoggingInvocationHandler(T myClass) {
        Method[] methods = myClass.getClass().getDeclaredMethods();
        this.loggableMethods = getLoggableMethods(methods);
        this.myClass = myClass;
    }

    private Set<Method> getLoggableMethods(Method[] methods) {
        Set<Method> annotatedLogMethods = new HashSet<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Log.class)) {
                annotatedLogMethods.add(method);
            }
        }
        return annotatedLogMethods;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (Method loggableMethod : loggableMethods) {
            if (ReflectionHelper.equalMethods(loggableMethod, method)) {
                logMethod(method, args);
                break;
            }
        }

        return method.invoke(myClass, args);
    }

    private void logMethod(Method method, Object[] args) {
        StringBuilder message = new StringBuilder("execute method: " + method.getName() + ", param:");
        for (Object arg : args) {
            message.append(" ").append(arg);
        }
        System.out.println(message);
    }
}
