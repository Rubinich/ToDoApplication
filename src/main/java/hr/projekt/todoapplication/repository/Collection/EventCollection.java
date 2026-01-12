package hr.projekt.todoapplication.repository.Collection;

import hr.projekt.todoapplication.model.event.Event;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventCollection implements Serializable {
    public List<Event> events = new ArrayList<>();
}
