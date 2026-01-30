package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.exceptions.StorageException;
import hr.projekt.todoapplication.model.event.Event;
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

    private final Storage<EventCollection> jsonStorage; // json
    private final EventDao  databaseStorage; // baza
    private EventCollection eventCollection; // program

    private EventRepository() {
        this.jsonStorage = new JsonStorage<>(EventCollection.class);
        this.databaseStorage = new EventDatabaseDao();
        this.eventCollection = new EventCollection();
    }
    public static EventRepository getInstance() {
        return Optional.ofNullable(instance).orElseGet(() -> {
            instance = new EventRepository();
            return instance;
        });
    }

    public Integer getEventCountForUser(String userId) {
        return databaseStorage.getEventCountForUser(userId);
    }

    public void loadEventsFromDatabaseForCurrentUser(String userId) {
        List<Event> eventsFromDb = databaseStorage.findByUserId(userId);
        eventCollection = new EventCollection();
        eventCollection.setEvents(new ArrayList<>(eventsFromDb));
    }

    public List<Event> getCachedEvents() {
        return new ArrayList<>(eventCollection.getEvents());
    }

    public void saveEventToEverywhere(Event event) {
        try {
            eventCollection.getEvents().add(event); // program
            jsonStorage.write(EVENTS_FILE, eventCollection); // json
            databaseStorage.save(event);  // baza podataka

        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void updateEventToEverywhere(Event event) {
        try{
            databaseStorage.update(event);
            List<Event> events = eventCollection.getEvents();
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getId().equals(event.getId())) {
                    events.set(i, event);
                    break;
                }
            }
            jsonStorage.write(EVENTS_FILE, eventCollection);

        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void deleteEventFromEverywhere(String eventId) {
        try {
            databaseStorage.delete(eventId);  // baza podataka
            eventCollection.getEvents().removeIf(e -> e.getId().equals(eventId));  // program
            jsonStorage.write(EVENTS_FILE, eventCollection); // json

        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void deleteAllEventsForUser(String userI) {
        try {
            databaseStorage.deleteAllEventsForUser(userI); // baza podataka
            EventCollection collection = jsonStorage.read(EVENTS_FILE).orElse(new EventCollection());
            collection.getEvents().removeIf(e -> e.getUserId().equals(userI)); // json
            jsonStorage.write(EVENTS_FILE, collection);
            eventCollection.getEvents().removeIf(e -> e.getUserId().equals(userI)); // program

        } catch(IOException | ClassNotFoundException e) {
            throw new StorageException(e);
        }
    }

    public void clearCache() {
        eventCollection = new EventCollection();
    }
}