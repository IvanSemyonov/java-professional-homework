package ru.otus.dataprocessor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.otus.model.Measurement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final String file;

    public ResourcesFileLoader(String fileName) {
        this.file = ClassLoader.getSystemResource(fileName).getFile();
    }

    @Override
    public List<Measurement> load() throws IOException {
        Gson gson = new Gson();
        try(InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
            return gson.fromJson(reader, new TypeToken<List<Measurement>>(){}.getType());
        }
    }
}
