package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.Planner;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EventViewController {
    private static final Logger log = LoggerFactory.getLogger(EventViewController.class);
    private static final String EVENT_CARD_PATH = "/hr/projekt/todoapplication/event/event-card-screen.fxml";

    @FXML private VBox eventContainer;
    private Planner planner;

    @FXML
    void initialize() {
        planner = new Planner();
        loadEventsOnScreen();
    }

    private void loadEventsOnScreen() {
        eventContainer.getChildren().clear();
        List<Event> events = planner.getCurrentUserEvents();

        if(events == null || events.isEmpty()) {
            planner.getCurrentUser().ifPresentOrElse(
                    user -> log.info("Korisnik {} nema događaja.", user.getUsername()),
                    () -> log.warn("Nema prijavljenog korisnika."));
            return;
        }

        for(Event event : events) {
            try{
                addEventCard(event);
            } catch (IOException e) {
                log.error("Greska pri ucitavanju kartice za dogadaj {} : {}", event.getTitle(), e.getMessage(), e);
            }
        }
        planner.getCurrentUser().ifPresent(user -> log.info("Prikazano {} događaja za korisnika {}", events.size(), user.getUsername()));
    }

    private void addEventCard(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(EVENT_CARD_PATH));
        Parent eventCard = loader.load();
        EventCardController controller = loader.getController();
        controller.setEvent(event);
        eventContainer.getChildren().add(eventCard);
    }
}
