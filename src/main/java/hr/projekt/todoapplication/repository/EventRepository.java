package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.collection.EventCollection;
import hr.projekt.todoapplication.repository.database.EventDao;
import hr.projekt.todoapplication.repository.database.EventDatabaseDao;
import hr.projekt.todoapplication.repository.storage.JsonStorage;
import hr.projekt.todoapplication.repository.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class EventRepository {
    private static final Logger logger = LoggerFactory.getLogger(EventRepository.class);
    private static final Path EVENTS_FILE = Path.of("data/events.json");

    private static EventRepository eventRepository;
    private final UserRepository userRepository;

    private final Storage<EventCollection> jsonStorage;
    private final EventDao databaseStorage;

    private EventRepository() {
        this.userRepository = UserRepository.getInstance();
        this.jsonStorage = new JsonStorage<>(EventCollection.class);
        this.databaseStorage = new EventDatabaseDao();
    }
    public static EventRepository getInstance() {
        if (eventRepository == null)
            eventRepository = new EventRepository();
        return eventRepository;
    }

    public void saveEvent(Event event) {
        try {
            EventCollection collection = jsonStorage.read(EVENTS_FILE).orElse(new EventCollection());
            collection.events.add(event); // program
            jsonStorage.write(EVENTS_FILE, collection); // json
            databaseStorage.save(event);  // baza podataka

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

}