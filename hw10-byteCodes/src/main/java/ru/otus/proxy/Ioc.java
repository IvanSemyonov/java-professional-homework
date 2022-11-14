package ru.otus.proxy;

import ru.otus.reflection.ReflectionHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Ioc {
    private Ioc() {
    }

    public static<T> T createClass(Class<? extends T> clazz) {
        InvocationHandler handler = new LoggingInvocationHandler<>(ReflectionHelper.instantiate(clazz));
        return (T) Proxy.newProxyInstance(Ioc.class.getClassLoader(),
                clazz.getInterfaces(), handler);
    }

}
