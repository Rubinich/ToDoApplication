package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.EventRepository;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.MenuLoader;
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
    @FXML private VBox menuContainer;

    private final EventRepository eventRepository = EventRepository.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();

    @FXML
    void initialize() {
        MenuLoader.loadMenuForCurrentUser(menuContainer);
        loadEventsOnScreen();
    }

    private void loadEventsOnScreen() {
        eventContainer.getChildren().clear();
        Optional<User> currentUser = userRepository.getCurrentUser();
        if(currentUser.isEmpty()) {
            log.error("Nema prijavljenog korisnika");
            return;
        }
        List<Event> events = eventRepository.getCurrentUserEvents();
        if(events == null) {
            log.error("Lista događaja je NULL!");
            return;
        }

        if(events.isEmpty()) {
            log.warn("Lista događaja je PRAZNA za korisnika: {}", currentUser.get().getUsername());
            return;
        }
        for(Event event: events) {
            try{
                addEventCard(event);
            } catch (IOException e) {
                log.error("Greška pri dodavanju: {}",e.getMessage(), e);
            }
        }
    }

    private void addEventCard(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(EVENT_CARD_PATH));
        Parent eventCard = loader.load();
        EventCardController controller = loader.getController();
        controller.setEvent(event);
        eventContainer.getChildren().add(eventCard);
    }
}