package hr.projekt.todoapplication.repository.collection;

import hr.projekt.todoapplication.model.event.Event;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventCollection implements Serializable {
    private List<Event> events = new ArrayList<>();

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Integer getEventCount() {
        return events.size();
    }
}
