package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.Collection.EventCollection;
import hr.projekt.todoapplication.repository.Storage.JsonStorage;
import hr.projekt.todoapplication.repository.Storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class EventRepository {
    private static final Logger log = LoggerFactory.getLogger(EventRepository.class);
    private static final Path EVENTS_FILE = Path.of("data/events.json");

    private final Storage<EventCollection> eventStorage;
    private final Map<String, Event> currentUserEvents;

    private final UserRepository userRepository;
    private static EventRepository instance;


    private EventRepository() {
        this.userRepository = UserRepository.getInstance();
        this.eventStorage = new JsonStorage<>(EventCollection.class);
        this.currentUserEvents = new HashMap<>();
    }
    // paziti za dretve
    public static EventRepository getInstance() {
        if(instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    public void loadEventsForCurrentUser() {
        Optional<User> currentUser = userRepository.getCurrentUser();
        if(currentUser.isEmpty()) {
            log.warn("Nema prijavljenog korisnika, ne mogu učitati događaje");
            return;
        }
        String username = currentUser.get().getUsername();
        this.currentUserEvents.clear();

        try{
            Optional<EventCollection> collection = eventStorage.read(EVENTS_FILE);
            if(collection.isPresent()) {
                collection.get().events.stream()
                        .filter(event -> username.equals(event.getOwnerUsername()))
                        .forEach(event -> currentUserEvents.put(event.getId(), event));
                log.info("Učitano {} događaja za korisnika: {}", currentUserEvents.size(), username);
            }
        } catch(ClassNotFoundException | IOException e) {
            log.error("Greska pri ucitavanju dogadaja: {}", e.getMessage(), e);
        }
    }

    public void addEvent(Event event) {
        try{
            EventCollection collection = eventStorage.read(EVENTS_FILE).orElse(new EventCollection());
            collection.events.add(event);
            eventStorage.write(EVENTS_FILE, collection);

            Optional<User> currentUser = userRepository.getCurrentUser();
            if(currentUser.isPresent() && event.getOwnerUsername().equals(currentUser.get().getUsername()))
                currentUserEvents.put(event.getId(), event);
            log.info("Dodan događaj: {} (vlasnik: {})", event.getTitle(), event.getOwnerUsername());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Event> findEventsByUsername(String username) {
        return currentUserEvents.values().stream()
                .filter(e -> e.getOwnerUsername().equals(username))
                .collect(Collectors.toList());
    }

    public void clearCurrentUserEvents(){
        this.currentUserEvents.clear();
    }
}
