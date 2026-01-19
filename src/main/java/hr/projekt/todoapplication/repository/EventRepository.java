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

    private static EventRepository instance;
    private final Storage<EventCollection> eventStorage;
    private final UserRepository userRepository;

    private List<Event> currentUserEvents;

    private EventRepository() {
        this.eventStorage = new JsonStorage<>(EventCollection.class);
        this.userRepository = UserRepository.getInstance();
        this.currentUserEvents = new ArrayList<>();
        log.info("EventRepository inicijaliziran");
    }

    public static EventRepository getInstance() {
        if(instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    public void loadEventsForCurrentUser() {
        Optional<User> currentUser = userRepository.getCurrentUser();
        if(currentUser.isEmpty()) {
            log.error("Nema prijavljenog korisnika!");
            return;
        }

        String userId = currentUser.get().getId();
        this.currentUserEvents.clear();
        try {
            Optional<EventCollection> collection = eventStorage.read(EVENTS_FILE);
            if(collection.isEmpty()) {
                log.warn("JSON datoteka je prazna ili ne postoji");
                return;
            }
            List<Event> userEvents = collection.get().events.stream()
                    .filter(e -> {
                        boolean matches = userId.equals(e.getOwnerId());
                        if(matches) {
                            log.debug("Pronađen događaj: {} (owner: {})", e.getTitle(), e.getOwnerId());
                        }
                        return matches;
                    })
                    .collect(Collectors.toList());
            for(Event e : userEvents) {
                currentUserEvents.add(e);
                log.debug("Dodan u memoriju: {} (ID: {})", e.getTitle(), e.getId());
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Greška pri učitavanju događaja: {}", e.getMessage(), e);
        }
    }

    public void addEvent(Event event) {
        if(event == null) {
            throw new IllegalArgumentException("Događaj ne može biti null");
        }

        try {
            EventCollection collection = eventStorage.read(EVENTS_FILE).orElse(new EventCollection());

            collection.events.add(event);
            eventStorage.write(EVENTS_FILE, collection);

            Optional<User> currentUser = userRepository.getCurrentUser();
            if(currentUser.isPresent() && event.getOwnerId().equals(currentUser.get().getId()))
                currentUserEvents.add(event);

            log.info("Događaj spremljen: {} (vlasnik: {})", event.getTitle(), event.getOwnerId());

        } catch (IOException | ClassNotFoundException e) {
            log.error("Greška pri dodavanju događaja: {}", e.getMessage());
            throw new RuntimeException("Nije moguće dodati događaj", e);
        }
    }

    public List<Event> getCurrentUserEvents() {
        if(currentUserEvents.isEmpty())
            loadEventsForCurrentUser();

        List<Event> result = new ArrayList<>(currentUserEvents);
        log.info("Vraćam {} događaja", result.size());
        return result;
    }

    public List<Event> findEventsByUsername(String username) {
        if(username == null || username.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Optional<User> currentUser = userRepository.getCurrentUser();
        if(currentUser.isPresent() && username.equals(currentUser.get().getUsername())) {
            return getCurrentUserEvents();
        }

        try {
            Optional<EventCollection> collection = eventStorage.read(EVENTS_FILE);
            if(collection.isEmpty()) {
                return Collections.emptyList();
            }

            return collection.get().events.stream()
                    .filter(e -> username.equals(e.getOwnerId()))
                    .collect(Collectors.toList());

        } catch (IOException | ClassNotFoundException e) {
            log.error("Greška pri traženju događaja: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Event> findAll() {
        try {
            Optional<EventCollection> collection = eventStorage.read(EVENTS_FILE);
            return collection.map(c -> new ArrayList<>(c.events)).orElse(new ArrayList<>());
        } catch (IOException | ClassNotFoundException e) {
            log.error("Greška pri učitavanju svih događaja: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}