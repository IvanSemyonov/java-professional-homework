package ru.otus.reflection;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

import java.lang.reflect.Method;
import java.util.*;

import static ru.otus.reflection.ReflectionHelper.*;

public class TestRunner {

    public <T> void run(Class<T> testClass) {
        Method[] methods = testClass.getDeclaredMethods();
        List<Method> testMethods = getAnnotatedMethods(methods, Test.class);
        List<Method> beforeMethods = getAnnotatedMethods(methods, Before.class);
        List<Method> afterMethods = getAnnotatedMethods(methods, After.class);

        int numberOfTests = testMethods.size();
        int passedTests = 0;

        for (Method testMethod : testMethods) {
            Object testObject = instantiate(testClass);
            try {
                beforeMethods.forEach(method -> callMethod(testObject, method.getName()));
                callMethod(testObject, testMethod.getName());
                passedTests++;
            } catch (Exception e) {
                System.out.println(testMethod.getName() + " failed");
                e.printStackTrace();
            } finally {
                afterMethods.forEach(method -> callMethod(testObject, method.getName()));
            }
        }
        System.out.println();
        System.out.printf("Passed %s of %s, failed %s", passedTests, numberOfTests, numberOfTests-passedTests);
    }
}
