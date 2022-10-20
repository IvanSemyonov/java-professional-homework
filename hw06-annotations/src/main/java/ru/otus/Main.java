package ru.otus;

import ru.otus.reflection.TestRunner;

public class Main {
    private static final TestRunner testRunner = new TestRunner();

    public static void main(String[] args) {
        testRunner.run(TestClass.class);
    }
}
