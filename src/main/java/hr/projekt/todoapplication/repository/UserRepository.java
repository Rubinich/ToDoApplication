package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.collection.UserCollection;
import hr.projekt.todoapplication.repository.database.UserDao;
import hr.projekt.todoapplication.repository.database.UserDatabaseDao;
import hr.projekt.todoapplication.repository.storage.JsonStorage;
import hr.projekt.todoapplication.repository.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final Path USERS_FILE = Path.of("data/users.json");
    private static UserRepository instance;
    private Optional<User> currentUser = Optional.empty();

    private final Storage<UserCollection> jsonStorage;
    private final UserDao databaseStorage;

    private UserRepository() {
        this.jsonStorage = new JsonStorage<>(UserCollection.class);
        this.databaseStorage = new UserDatabaseDao();
    }
    public static UserRepository getInstance() {
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    public boolean saveUser(User user) {
        try {
            UserCollection collection = jsonStorage.read(USERS_FILE).orElse(new UserCollection());
            boolean exists = collection.users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()));
            if(exists)
                return false;
            collection.users.add(user);  // program
            jsonStorage.write(USERS_FILE, collection);  // json
            databaseStorage.save(user);  // baza podataka
            return true;

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Kritična greška: {}", e.getMessage());
            return false;
        }
    }

    public Optional<User> authenticate(String username, String password) throws IOException {
        Optional<User> foundUser = databaseStorage.findByUsernameAndPassword(username, password);
        if(foundUser.isPresent()) {
            this.currentUser = foundUser;
            logger.info("Uspjesna prijava iz baze.");
        } else
            logger.info("Neuspjesna prijava iz baze.");

//        Optional<UserCollection> collection = jsonStorage.read(USERS_FILE);
//        Optional<User> foundUser = collection.get().users.stream()
//                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
//                .findFirst();

        return foundUser;
    }

    public Optional<User> getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = Optional.empty();
    }
}
