package hr.projekt.todoapplication.repository.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public sealed interface Storage<T> permits BinaryStorage, JsonStorage {
    void write(Path path, T object) throws IOException;
    Optional<T> read(Path usersFile) throws IOException, ClassNotFoundException;
}
