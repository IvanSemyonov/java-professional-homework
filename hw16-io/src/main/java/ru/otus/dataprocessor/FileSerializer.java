package ru.otus.dataprocessor;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final String fileURI;

    public FileSerializer(String fileName) {
        this.fileURI = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) throws IOException {
        //формирует результирующий json и сохраняет его в файл
        Gson gson = new Gson();
        try(OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(fileURI))) {
            gson.toJson(data, writer);
        }
    }
}
