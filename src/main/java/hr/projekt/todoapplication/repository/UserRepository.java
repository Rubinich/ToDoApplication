package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

//TODO moguce pretvori u interface za JsonRepository, XmlRepository i DataBaseRepository
public class UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private static final Path DATA_FILE = Path.of("data/users.json");
    private static final Path BACKUP_FILE = Path.of("data/backup.bin");

    private final Storage<UserCollection> fileStorage;
    private final Storage<UserCollection> backupStorage;
    private final Set<User> users;

    public UserRepository() {
        this.fileStorage = new JsonStorage<>(UserCollection.class);
        this.backupStorage = new BinaryStorage<>();
        this.users = new HashSet<>();
        loadAll();
    }

    private void loadAll() {
        try{
            Optional<UserCollection> collection = fileStorage.read(DATA_FILE);
            collection.ifPresent(c -> {
                if (c.users != null) {
                    users.addAll(c.users);
                    log.info("Učitano {} korisnika iz {}", users.size(), DATA_FILE);
                }
            });
        } catch (IOException | ClassNotFoundException e) {
            log.error("Greska pri ucitavanju korisnika: {}", e.getMessage(), e);
            throw new RuntimeException("Nije moguce ucitati korisnike", e);
        }
    }

    public void saveAll() {
        try{
            UserCollection collection = new UserCollection();
            collection.users = users;
            fileStorage.write(DATA_FILE, collection);
            log.debug("Spremljeno {} korisnika u {}", users.size(), DATA_FILE);
        } catch (IOException e) {
            log.error("Greska pri spremanju korisnika: {}", e.getMessage(), e);
            throw new RuntimeException("Nije moguce spremiti korisnike", e);
        }
    }

    public void add(User user) {
        if(user == null)
            throw new IllegalArgumentException("Korisnik ne moze biti null");
        users.add(user);
        saveAll();
        log.info("Dodan korisnik: {}", user.getUsername());
    }

    public void update(User user) {
        saveAll();
        log.debug("Azuriran korisnik: {}", user.getUsername());
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public void createBackup() {
        try {
            UserCollection collection = new UserCollection();
            collection.users = users;
            backupStorage.write(BACKUP_FILE, collection);
            log.info("Backup uspješno kreiran: {}", BACKUP_FILE);
        } catch (Exception e) {
            log.error("Greška pri kreiranju backupa: {}", e.getMessage(), e);
            throw new RuntimeException("Nije moguće kreirati backup", e);
        }
    }

    public void restoreFromBackup() {
        try {
            Optional<UserCollection> collection = backupStorage.read(BACKUP_FILE);
            collection.ifPresent(c -> {
                if(c.users != null) {
                    users.clear();
                    users.addAll(c.users);
                    saveAll();
                    log.info("Backup vraćen: {} korisnika", users.size());
                }
            });
        } catch (Exception e) {
            log.error("Greška pri vraćanju backupa: {}", e.getMessage(), e);
            throw new RuntimeException("Nije moguće vratiti backup", e);
        }
    }

    public int count(){
        return users.size();
    }

    public Set<User> findAll() {
        return Collections.unmodifiableSet(users);
    }
}
