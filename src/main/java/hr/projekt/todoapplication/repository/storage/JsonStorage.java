package hr.projekt.todoapplication.repository.storage;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class JsonStorage<T> implements Storage<T> {
    private static final Logger logger = LoggerFactory.getLogger(JsonStorage.class);
    private static final Path USERS_FILE = Path.of("data/users.json");

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

    @Override
    public void write(Path path, T object) throws IOException {
        if(path.getParent() != null)
            Files.createDirectories(path.getParent());
        String json = jsonb.toJson(object);
        Files.writeString(path, json);
    }

    @Override
    public Optional<T> read(Path path) throws IOException {
        if(!Files.exists(path))
            return Optional.empty();
        String json = Files.readString(path);
        T object = jsonb.fromJson(json, type);
        return Optional.of(object);
    }
}
