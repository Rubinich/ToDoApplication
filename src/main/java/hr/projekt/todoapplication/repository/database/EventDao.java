package hr.projekt.todoapplication.repository.database;

import hr.projekt.todoapplication.model.event.Event;

import java.io.IOException;
import java.util.List;

public interface EventDao {
    void save(Event event);
    List<Event> findByUserId(String userId);
    void update(Event event);
    void delete(String id);
    Integer getEventCountForUser(String userId);
    void deleteAllEventsForUser(String userId);
}
