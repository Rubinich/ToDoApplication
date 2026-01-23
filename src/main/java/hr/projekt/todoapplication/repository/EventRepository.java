package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.exceptions.StorageException;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.collection.EventCollection;
import hr.projekt.todoapplication.repository.database.EventDao;
import hr.projekt.todoapplication.repository.database.EventDatabaseDao;
import hr.projekt.todoapplication.repository.storage.JsonStorage;
import hr.projekt.todoapplication.repository.storage.Storage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class EventRepository {
    private static final Path EVENTS_FILE = Path.of("data/events.json");

    private static EventRepository instance;
    private final UserRepository userRepository;

    private final Storage<EventCollection> jsonStorage;
    private final EventDao  databaseStorage;

    private EventRepository() {
        this.userRepository = UserRepository.getInstance();
        this.jsonStorage = new JsonStorage<>(EventCollection.class);
        this.databaseStorage = new EventDatabaseDao();
    }
    public static EventRepository getInstance() {
        return Optional.ofNullable(instance).orElseGet(() -> {
            instance = new EventRepository();
            return instance;
        });
    }

    public void saveEvent(Event event) {
        try {
            EventCollection collection = jsonStorage.read(EVENTS_FILE).orElse(new EventCollection());
            collection.getEvents().add(event); // program
            jsonStorage.write(EVENTS_FILE, collection); // json
            databaseStorage.save(event);  // baza podataka

        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException(e);
        }
    }

    public List<Event> getCurrentUserEvents() {
        Optional<User> currentUser = userRepository.getCurrentUser();
        return findEventsByUserId(currentUser.get().getId());
    }

    public List<Event> findEventsByUserId(String userId) {
        try {
            return databaseStorage.findByUserId(userId);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

}