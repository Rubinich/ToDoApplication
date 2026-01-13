package hr.projekt.todoapplication.model;

import hr.projekt.todoapplication.ToDoApplication;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.DataManager;
import jakarta.json.bind.annotation.JsonbTransient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class Planner {
    @JsonbTransient
    private static final Logger log = LoggerFactory.getLogger(Planner.class);
    private final DataManager dataManager;

    public Planner() {
        this.dataManager = new DataManager();
        log.info("Planner inicijaliziran s {} korisnika", dataManager.getUserCount());
    }

    public Planner(DataManager dataManager){
        this.dataManager = dataManager;
    }

    public List<Event> getCurrentUserEvents(){
        return ToDoApplication.getCurrentUser()
                .map(user -> dataManager.getEventsByUsername(user.getUsername()))
                .orElse(List.of());
    }

    public void addEventToCurrentUser(Event event) {
        User user = ToDoApplication.getCurrentUser().orElseThrow(() -> new IllegalArgumentException("Nema prijavljenog korisnika."));
        dataManager.addEvent(event, user);
        log.info("Događaj '{}' dodan korisniku '{}'", event.getTitle(), user.getUsername());
    }
}
