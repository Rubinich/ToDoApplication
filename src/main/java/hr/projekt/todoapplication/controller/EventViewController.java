package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.EventRepository;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.MenuLoader;
import javafx.application.Platform;
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
    private static final Logger logger = LoggerFactory.getLogger(EventViewController.class);
    private static final String EVENT_CARD_PATH = "/hr/projekt/todoapplication/event/event-card-screen.fxml";

    @FXML private VBox eventContainer;
    @FXML private VBox menuContainer;

    private final EventRepository eventRepository = EventRepository.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();

    @FXML
    void initialize() {
        MenuLoader.loadMenuForCurrentUser(menuContainer);
        loadEventsOnScreen();
    }

    private void loadEventsOnScreen() {
        Optional<User> currentUser = userRepository.getCurrentUser();
        if(currentUser.isEmpty()) {
            logger.error("Nema prijavljenog korisnika");
            return;
        }
        logger.info("Učitavam događaje za: {}", currentUser.get().getUsername());

        List<Event> events = eventRepository.getCachedEvents();
        eventContainer.getChildren().clear();
        for (Event event : events)
            addEventCard(event);
    }

    private void addEventCard(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(EVENT_CARD_PATH));
            Parent eventCard = loader.load();
            EventCardController controller = loader.getController();
            controller.setEvent(event);
            controller.setOnEventChanged(this::loadEventsOnScreen);
            eventContainer.getChildren().add(eventCard);
        } catch (IOException e) {
            logger.info("Greška prilikom dodavanje kartice događaja: {}", e.getMessage());
        }
    }
}