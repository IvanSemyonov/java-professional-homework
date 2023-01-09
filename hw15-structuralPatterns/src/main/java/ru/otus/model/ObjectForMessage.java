package ru.otus.model;

import java.util.List;

public class ObjectForMessage {
    private List<String> data;

    public List<String> getData() {
        return List.copyOf(data);
    }

    public void setData(List<String> data) {
        this.data = List.copyOf(data);
    }
}
