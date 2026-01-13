package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class DataManager {
    private static final Logger log = LoggerFactory.getLogger(DataManager.class);
    private static final Path BACKUP_FILE = Path.of("data/backup.bin");
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    //private final Storage<BackupData> backupStorage;

    public DataManager() {
        this.userRepository = new UserRepository();
        this.eventRepository = new EventRepository();
        //this.backupStorage = new BackupStorage();
    }

    public DataManager(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        //this.backupStorage = new BinaryStorage<>();
    }

    public void addUser(User user) {
        userRepository.addUserToRam(user);
        log.info("Dodan korisnik: {}", user.getUsername());
    }

    public void addEvent(Event event, User user) {
        event.setOwnerUsername(user.getUsername());
        eventRepository.addEventToRam(event);
        log.info("Dodan dogadaj: {}", user.getUsername());
    }

    public List<Event> getEventsByUsername(String username) {
        return eventRepository.findEventsByUsername(username);
    }

    public int getUserCount() {
        return userRepository.count();
    }

    public Set<User> findAllUsers() {
        return userRepository.findAll();
    }
}
