package hr.projekt.todoapplication.model;

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
    @JsonbTransient
    private Optional<User> currentUser = Optional.empty();
    private final DataManager dataManager;

    public Planner() {
        this.dataManager = new DataManager();
        log.info("Planner inicijaliziran s {} korisnika", dataManager.getUserCount());

        if (dataManager.getUserCount() > 0) {
            User firstUser = dataManager.findAllUsers().iterator().next();
            currentUser = Optional.of(firstUser);
            log.info("Auto-login: {}", firstUser.getUsername());
        } else {
            log.warn("Nema korisnika u bazi podataka!");
        }
    }

    public Planner(DataManager dataManager){
        this.dataManager = dataManager;
    }

    public List<Event> getCurrentUserEvents(){
        return currentUser
                .map(user -> dataManager.getEventsByUsername(user.getUsername()))
                .orElse(List.of());
    }

    public void addEventToCurrentUser(Event event) {
        User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Nema prijavljenog korisnika."));
        dataManager.addEvent(event, user);
        log.info("Događaj '{}' dodan korisniku '{}'", event.getTitle(), user.getUsername());
    }

    /**
     * Odjavljuje trenutno prijavljenog korisnika iz sustava.
     */
    public void logout() {
        currentUser.ifPresent(user -> {
            log.info("Korisnik {} odjavljen.", user.getUsername());
            System.out.println("Uspjesna odjava korisnika " + user.getUsername());
        });
        currentUser = Optional.empty();
    }

    public Optional<User> getCurrentUser() {
        return currentUser;
    }
}
