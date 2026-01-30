package hr.projekt.todoapplication.util;

import hr.projekt.todoapplication.exceptions.DatabaseException;
import hr.projekt.todoapplication.model.user.AdminUser;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class InitialDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);
    private static final String DEFAULT_ADMIN_FILE = "src/main/resources/default-admin.properties";

    private InitialDataLoader() {}

    public static void loadDefaultAdmin() {
        loadProperties().ifPresentOrElse(
                InitialDataLoader::processAdmin,
                () -> logger.error("Nije moguće učitati {}", DEFAULT_ADMIN_FILE)
        );
    }

    private static Optional<Properties> loadProperties() {
        try(FileReader reader = new FileReader(DEFAULT_ADMIN_FILE)) {
            Properties properties = new Properties();
            properties.load(reader);
            return Optional.of(properties);

        } catch (IOException e) {
            logger.error("Greška pri učitavanju file-a: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static void processAdmin(Properties properties) {
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        if(username == null || username.isBlank() || password == null || password.isBlank()) {
            logger.error("Admin username ili password nije definiran");
            return;
        }

        UserRepository repository = UserRepository.getInstance();
        try{
            Optional<User> exists = repository.authenticate(username, password);
            if(exists.isPresent()) {
                return;
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        }

        AdminUser admin = new AdminUser(username, password);
        boolean saved = repository.saveUser(admin);

        if (saved) {
            logger.info("Admin {} uspješno kreiran!", username);
        } else {
            logger.error("Greška pri kreiranju admin korisnika");
        }
    }
}
