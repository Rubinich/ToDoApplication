package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.controller.LoginController;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.Collection.UserCollection;
import hr.projekt.todoapplication.repository.Storage.JsonStorage;
import hr.projekt.todoapplication.repository.Storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

//TODO moguce pretvori u interface za JsonRepository, XmlRepository i DataBaseRepository
public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private static final Path USERS_FILE = Path.of("data/users.json");
    private final Storage<UserCollection> userStorage;
    private final Set<User> users;
    private static UserRepository instance;

    public UserRepository() {
        this.userStorage = new JsonStorage<>(UserCollection.class);
        this.users = new HashSet<>();
        loadUsersFromStorage();
    }

    public static UserRepository getInstance() {
        if(instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private void loadUsersFromStorage() {
        try {
            Optional<UserCollection> collection = userStorage.read(USERS_FILE);
            if (collection.isPresent()) {
                UserCollection userCollection = collection.get();
                if (userCollection.users != null && !userCollection.users.isEmpty()) {
                    users.addAll(userCollection.users);
                    log.info("Učitano {} korisnika", users.size());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Greska pri ucitavanju korisnika: {}", e.getMessage(), e);
        }
    }

    public void addUserToRam(User user) {
        if(user == null)
            throw new IllegalArgumentException("Korisnik ne moze biti null");
        users.add(user);
        addUsersToStorage();
        log.info("Dodan korisnik: {}", user.getUsername());
    }

    private void addUsersToStorage() {
        try{
            UserCollection collection = new UserCollection();
            collection.users = new HashSet<>(users);
            userStorage.write(USERS_FILE, collection);
            log.debug("Spremljeno {} korisnika u {}", users.size(), USERS_FILE);
        } catch(IOException e) {
            log.error("Greska pri spremanju korisnika: {}", e.getMessage(), e);
            throw new RuntimeException("Nije moguće spremiti korisnike", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        if(username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public Set<User> findAll() {
        return Collections.unmodifiableSet(users);
    }

    public int count() {
        return users.size();
    }

    public Optional<User> authenticate(String username, String password) {
        logger.info("Authenticating: username={}, password={}", username, "***");
        logger.info("Total users to check: {}", users.size());

        if(username == null || password == null) {
            logger.warn("Username or password is null");
            return Optional.empty();
        }

        // Debug: ispiši sve korisnike
        users.forEach(u ->
                logger.debug("Checking user: {} with password: {}", u.getUsername(), u.getPassword())
        );

        Optional<User> result = users.stream()
                .filter(user -> {
                    boolean usernameMatch = user.getUsername().equals(username);
                    boolean passwordMatch = user.getPassword().equals(password);
                    logger.debug("User {}: username match={}, password match={}",
                            user.getUsername(), usernameMatch, passwordMatch);
                    return usernameMatch && passwordMatch;
                })
                .findFirst();

        logger.info("Authentication result: {}", result.isPresent() ? "SUCCESS" : "FAILED");
        return result;
    }
}
