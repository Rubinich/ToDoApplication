package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.model.event.Event;
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
    private final Map<String, Event> events;

    public EventRepository() {
        this.eventStorage = new JsonStorage<>(EventCollection.class);
        this.events = new HashMap<>();
        loadEventsFromStorage();
    }

    private void loadEventsFromStorage() {
        try{
            Optional<EventCollection> collection = eventStorage.read(EVENTS_FILE);
            if(collection.isPresent()) {
                EventCollection eventCollection = collection.get();
                if(eventCollection.events != null && !eventCollection.events.isEmpty()) {
                    for(Event event : eventCollection.events) {
                        events.put(event.getId(), event);
                    }
                    log.info("Ucitano {} dogadaja", events.size());
                }
            }
        } catch(ClassNotFoundException | IOException e) {
            log.error("Greska pri ucitavanju dogadaja: {}", e.getMessage(), e);
        }
    }

    public void addEventToRam(Event event) {
        if(event == null)
            throw new IllegalArgumentException("Dogadaj ne moze biti null.");
        events.put(event.getId(), event);
        addEventToStorage();
        log.info("Dodan dogadaj: {} (vlasnik: {})", event.getTitle(), event.getOwnerUsername());
    }

    private void addEventToStorage() {
        try{
            EventCollection collection = new EventCollection();
            collection.events = new ArrayList<>(events.values());
            eventStorage.write(EVENTS_FILE, collection);
        } catch(IOException e) {
            log.error("Greska pri spremanju dogadaja: {}", e.getMessage(), e);
        }
    }

    public List<Event> findEventsByUsername(String username) {
        return events.values().stream()
                .filter(e -> e.getOwnerUsername().equals(username))
                .collect(Collectors.toList());
    }
}
