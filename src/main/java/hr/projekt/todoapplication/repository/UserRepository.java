package hr.projekt.todoapplication.repository;

import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.util.JsonStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
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

    private final JsonStorage<UserCollection> storage;
    private final Set<User> users;


    public UserRepository() {
        this.storage = new JsonStorage<>(UserCollection.class);
        this.users = new HashSet<>();
        loadAll();
    }

    private void loadAll() {
        try{
            UserCollection collection = storage.readFromFile(DATA_FILE);
            if(collection != null && collection.users != null) {
                users.addAll(collection.users);
                log.info("Ucitano {} korisnika iz {}", users.size(), DATA_FILE);
            } else {
                log.info("Nema postojecih podataka.");
            }
        } catch (IOException e) {
            log.error("Greska pri ucitavanju korisnika: {}", e.getMessage(), e);
            throw new RuntimeException("Nije moguce ucitati korisnike", e);
        }
    }

    public void saveAll() {
        try{
            UserCollection collection = new UserCollection();
            collection.users = users;
            storage.writeToFile(DATA_FILE, collection);
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
            storage.writeToBinary(BACKUP_FILE, collection);
            log.info("Backup uspješno kreiran: {}", BACKUP_FILE);
        } catch (Exception e) {
            log.error("Greška pri kreiranju backupa: {}", e.getMessage(), e);
            throw new RuntimeException("Nije moguće kreirati backup", e);
        }
    }

    public void restoreFromBackup() {
        try {
            UserCollection collection = storage.readFromBinary(BACKUP_FILE);
            if (collection == null) {
                log.warn("Backup datoteka ne postoji: {}", BACKUP_FILE);
                throw new RuntimeException("Backup datoteka ne postoji");
            }
            users.clear();
            users.addAll(collection.users);
            saveAll();
            log.info("Backup uspješno vraćen: {} korisnika", users.size());
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
