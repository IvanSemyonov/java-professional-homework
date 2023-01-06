package ru.otus.dataprocessor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.otus.model.Measurement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        Gson gson = new Gson();
        try(InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName))) {
            return gson.fromJson(reader, new TypeToken<List<Measurement>>(){}.getType());
        } catch (IOException ex) {
            throw new FileProcessException(ex);
        }
    }
}
