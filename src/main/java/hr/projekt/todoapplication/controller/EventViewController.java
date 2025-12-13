package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.Planner;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EventViewController {
    private static final Logger log = LoggerFactory.getLogger(EventViewController.class);

    @FXML
    private VBox eventContainer;

    private Planner planner;

    @FXML
    void initialize() {
        planner = new Planner();
        loadEvents();
    }

    private void loadEvents() {
        eventContainer.getChildren().clear();
        Optional<User> currentUser = planner.getCurrentUser();

        if(currentUser.isEmpty()) {
            log.warn("Nema prijavljenog korisnika.");
            return;
        }

        List<Event> events = currentUser.get().getEvents();
        if(events == null || events.isEmpty()) {
            log.info("Korisnik {} nema dogadaja.", currentUser.get().getUsername());
            return;
        }

        for(Event event : events) {
            try{
                addEventCard(event);
            } catch (IOException e) {
                log.error("Greska pri ucitavanju kartice za dogadaj {} : {}", event.getTitle(), e.getMessage(), e);
            }
        }
        log.info("Prikazano {} dogadaja za korisnika {}", events.size(), currentUser.get().getUsername());
    }

    private void addEventCard(Event event) throws IOException {
        String fxmlPath = "/hr/projekt/todoapplication/event/event-card.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent eventCard = loader.load();
        EventCardController controller = loader.getController();
        controller.setEvent(event);
        eventContainer.getChildren().add(eventCard);
    }
}
