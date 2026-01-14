package hr.projekt.todoapplication.util;

import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.EventRepository;
import hr.projekt.todoapplication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static SessionManager instance;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private SessionManager() {
        this.userRepository = UserRepository.getInstance();
        this.eventRepository = EventRepository.getInstance();
    }
    // pazi za dretve
    public static SessionManager getInstance() {
        if(instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Optional<User> login(String username, String password) {
        Optional<User> currentUser = userRepository.authenticate(username, password);
        if(currentUser.isPresent()) {
            eventRepository.loadEventsForCurrentUser();
            logger.info("Uspješna prijava korisnika: {} ({} događaja učitano)", username);
        }
        return currentUser;
    }

    public void logout() {
        Optional<User> currentUser = userRepository.getCurrentUser();
        if(currentUser.isPresent()) {
            eventRepository.clearCurrentUserEvents();
            userRepository.logout();
        }
    }
}
