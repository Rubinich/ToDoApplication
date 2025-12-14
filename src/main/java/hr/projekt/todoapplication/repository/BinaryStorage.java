package hr.projekt.todoapplication.repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class BinaryStorage<T> implements Storage<T>{

    @Override
    public void write(Path path, T object) throws IOException {
        if(path.getParent() != null)
            Files.createDirectories(path.getParent());
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            out.writeObject(object);
        }
    }

    @Override
    public Optional<T> read(Path path) throws IOException, ClassNotFoundException {
        if(!Files.exists(path))
            return Optional.empty();
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            T object = (T) in.readObject();
            return Optional.of(object);
        }
    }

}
