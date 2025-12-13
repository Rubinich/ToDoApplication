package hr.projekt.todoapplication.util;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonStorage<T> {

    private final Class<T> type;
    private final Jsonb jsonb;

    public JsonStorage(Class<T> type) {
        this.type = type;

        JsonbConfig config = new JsonbConfig()
                .withNullValues(false)
                .withFormatting(true)
                .withEncoding("UTF-8");

        this.jsonb = JsonbBuilder.create(config);
    }

    public void writeToFile(Path path, T object) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        String json = jsonb.toJson(object);
        Files.writeString(path, json);
    }

    public T readFromFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            return null;
        }

        String json = Files.readString(path);
        return jsonb.fromJson(json, type);
    }

    public void writeToBinary(Path path, T object) throws IOException, ClassCastException {
        if(path.getParent() != null)
            Files.createDirectories(path.getParent());
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            out.writeObject(object);
        }
    }

    public T readFromBinary(Path path) throws IOException, ClassNotFoundException {
        if(!Files.exists(path))
            return null;
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (T) in.readObject();
        }
    }
}
