package ru.otus;

import ru.otus.annotations.Log;

public class TestLogging implements TestLoggingInterface {

    @Log
    @Override
    public void calculation(int param1) {

    }

    @Override
    public void calculation(int param1, int param2) {

    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {

    }
}
