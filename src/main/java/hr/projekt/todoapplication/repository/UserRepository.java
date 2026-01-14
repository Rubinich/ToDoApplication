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
    private static final Path USERS_FILE = Path.of("data/users.json");

    private final Storage<UserCollection> userStorage;
    private static UserRepository instance;

    private Optional<User> currentUser = Optional.empty();

    private UserRepository() {
        this.userStorage = new JsonStorage<>(UserCollection.class);
    }
    // pazi za dretve
    public static UserRepository getInstance() {
        if(instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void addUser(User user) {
        if(user == null)
            throw new IllegalArgumentException("Korisnik ne može biti null");
        try {
            UserCollection collection = userStorage.read(USERS_FILE).orElse(new UserCollection());
            boolean exists = collection.users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()));
            if(exists)
                throw new IllegalArgumentException("Korisnik već postoji: " + user.getUsername());
            collection.users.add(user);
            userStorage.write(USERS_FILE, collection);
            logger.info("Dodan novi korisnik: {}", user.getUsername());

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Greška pri dodavanju korisnika: {}", e.getMessage());
            throw new RuntimeException("Nije moguće dodati korisnika", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        if(username == null || username.trim().isEmpty())
            return Optional.empty();
        try {
            Optional<UserCollection> collection = userStorage.read(USERS_FILE);
            if(collection.isEmpty())
                return Optional.empty();
            return collection.get().users.stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(username))
                    .findFirst();

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Greška pri traženju korisnika: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        if(currentUser != null) {
            logger.info("Odjava korisnika: {}", currentUser.get().getUsername());
            this.currentUser = Optional.empty();
        }
    }

    public Set<User> findAll() {
        try {
            Optional<UserCollection> collection = userStorage.read(USERS_FILE);
            return collection.map(c -> new HashSet<>(c.users)).orElse(new HashSet<>());
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Greška pri učitavanju svih korisnika: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    public Optional<User> authenticate(String username, String password) {
        if(username == null || password == null) {
            logger.warn("Username or password is null");
            return Optional.empty();
        }

        try{
            Optional<UserCollection> collection = userStorage.read(USERS_FILE);
            if(collection.isEmpty()) {
                logger.warn("Datoteka korisnika ne postoji");
                return Optional.empty();
            }
            Optional<User> foundUser = collection.get().users.stream()
                    .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                    .findFirst();
            if(foundUser.isPresent()) {
                this.currentUser = foundUser;
                logger.info("Uspješna prijava: {}", username);
            } else {
                logger.warn("Neuspješna prijava: {}", username);
            }
            return foundUser;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Greška pri autentifikaciji: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void setCurrentUser(Optional<User> currentUser) {
        this.currentUser = currentUser;
    }
}
