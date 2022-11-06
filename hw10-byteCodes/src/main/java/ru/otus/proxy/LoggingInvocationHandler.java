package ru.otus.proxy;

import ru.otus.annotations.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LoggingInvocationHandler<T> implements InvocationHandler {
    private final T myClass;

    LoggingInvocationHandler(T myClass) {
        this.myClass = myClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method = myClass.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (method.isAnnotationPresent(Log.class)) {
            logMethod(method, args);
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
