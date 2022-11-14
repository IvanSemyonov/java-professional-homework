package ru.otus;

import ru.otus.proxy.Ioc;

public class Main {
    public static void main(String[] args) {
        TestLoggingInterface testLogging = Ioc.createClass(TestLogging.class);
        testLogging.calculation(1);
        testLogging.calculation(1, 2);
        testLogging.calculation(1, 2, "param3");

    }
}
