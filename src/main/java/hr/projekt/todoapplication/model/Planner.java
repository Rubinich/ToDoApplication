package hr.projekt.todoapplication.model;

import hr.projekt.todoapplication.exceptions.EventInPastException;
import hr.projekt.todoapplication.exceptions.InvalidEventInputException;
import hr.projekt.todoapplication.exceptions.LoginException;
import hr.projekt.todoapplication.exceptions.UsernameExistsException;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.event.EventCategory;
import hr.projekt.todoapplication.model.event.PriorityLevel;
import hr.projekt.todoapplication.model.user.AdminUser;
import hr.projekt.todoapplication.model.user.RegularUser;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.UserRepository;
import jakarta.json.bind.annotation.JsonbTransient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class Planner {
    @JsonbTransient
    private static final Logger log = LoggerFactory.getLogger(Planner.class);
    @JsonbTransient
    private Optional<User> currentUser = Optional.empty();
    private final UserRepository userRepository;

    public Planner() {
        this.userRepository = new UserRepository();
        log.info("Planner inicijaliziran s {} korisnika", userRepository.count());

        if (userRepository.count() > 0) {
            User firstUser = userRepository.findAll().iterator().next();
            currentUser = Optional.of(firstUser);
            log.info("Auto-login: {} ({} događaja)",
                    firstUser.getUsername(),
                    firstUser.getEvents().size());
        } else {
            log.warn("Nema korisnika u bazi podataka!");
        }
    }

    public Planner(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Set<User> getProgramUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUser(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Stvara novog korisnika prema unosu u konzolu.
     * <p>
     *     Ako korisnicko ime vec postoji, baca se {@link UsernameExistsException}.
     * </p>
     *
     * @param sc objekt tipa {@link Scanner} za unos korisnika
     * @throws UsernameExistsException ako korisnicko ime vec postoji
     */
    public void createUser(Scanner sc) {
        System.out.print("Tip korisnika (admin / user): ");
        String type = sc.nextLine().trim();
        System.out.print("Korisnicko ime: ");
        String username = sc.nextLine().trim();
        if (userRepository.existsByUsername(username)) {
            log.warn("Pokusaj registracije postojeceg korisnika: {}", username);
            throw new UsernameExistsException("Korisnik s imenom '" + username + "' vec postoji!");
        }
        System.out.print("Lozinka: ");
        String password = sc.nextLine().trim();
        User newUser = type.equalsIgnoreCase("admin") ? new AdminUser(username) : new RegularUser(username);
        newUser.setPassword(password);
        userRepository.add(newUser);
        System.out.println("Stvoren korisnik: " + username);
        log.info("Stvoren novi korisnik: {}", username);
    }

    /**
     * Prijavljuje korisnika prema unesenom imenu i lozinci.
     *
     * @param sc objekt tipa {@link Scanner} za unos podataka
     * @return prijavljeni korisnik
     * @throws LoginException ako korisnik ne postoji ili je lozinka pogresna
     */
    public User loginUser(Scanner sc) throws LoginException {
        System.out.print("Korisnicko ime: ");
        String username = sc.nextLine().trim();

        System.out.print("Lozinka: ");
        String password = sc.nextLine().trim();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new LoginException("Korisnik ne postoji"));

        if (!user.getPassword().equals(password)) {
            log.warn("Neuspjesan pokusaj prijave za korisnika: {}", username);
            throw new LoginException("Pogrešna lozinka.");
        }
        currentUser = Optional.of(user);
        log.info("Uspjesna prijava korisnika {}.", username);
        System.out.println("Uspjesno prijavljeni!");

        return user;
    }

    /**
     * Odjavljuje trenutno prijavljenog korisnika iz sustava.
     */
    public void logout() {
        currentUser.ifPresent(user -> {
            log.info("Korisnik {} odjavljen.", user.getUsername());
            System.out.println("Uspjesna odjava korisnika " + user.getUsername());
        });
        currentUser = Optional.empty();
    }

    public Optional<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * Stvara novi dogadaj za trenutno prijavljenog korisnika.
     * <p>
     *     Ako je datum u proslosti, baca se {@link EventInPastException}.
     *     Ako je unos neispravan, baca se {@link InvalidEventInputException}.
     * </p>
     *
     * @param sc objekt tipa {@link Scanner} za unos podataka
     */
    public void createEvent(Scanner sc) {
        try {
            String title = EventInputHelper.prompt(sc, "Naslov dogadaja");
            String description = EventInputHelper.prompt(sc, "Opis dogadaja");
            LocalDateTime dueDate = EventInputHelper.promptDate(sc);
            EventCategory category = parseEnum(EventInputHelper.prompt(sc, "Kategorija (OSOBNO, POSAO, SHOPPING, OSNOVNO)"), EventCategory.class, EventCategory.OSNOVNO);
            PriorityLevel priority = parseEnum(EventInputHelper.prompt(sc, "Prioritet (IGNORIRAJ, ZADANO, VAZNO)"), PriorityLevel.class, PriorityLevel.ZADANO);
            if (dueDate.isBefore(LocalDateTime.now())) {
                throw new EventInPastException("Datum dogadaja ne moze biti u proslosti!");
            }
            Event newEvent = new Event.EventBuilder(title, description, dueDate).category(category).priority(priority).build();
            User user = currentUser.get();
            user.addEvent(newEvent);
            userRepository.update(user);

            log.info("Uspjesno kreiran dogadaj: {}", title);
            System.out.println("Dogadaj uspjesno kreiran!");
        } catch (InvalidEventInputException | EventInPastException e) {
            log.error("Greska pri unosu dogadaja: {}", e.getMessage());
            System.out.println("Greska: " + e.getMessage());
        }
    }

    private <T extends Enum<T>> T parseEnum(String input, Class<T> enumType, T defaultValue) {
        try {
            return Enum.valueOf(enumType, input.trim().toUpperCase());
        } catch (IllegalArgumentException _) {
            System.out.println("Nepoznata vrijednost, postavljena na: " + defaultValue);
            return defaultValue;
        }
    }
    public void createBackup() {
        try {
            userRepository.createBackup();
            System.out.println("Backup uspješno kreiran");
            log.info("Backup uspješno kreiran");
        } catch (Exception e) {
            System.out.println("Greška pri kreiranju backupa: " + e.getMessage());
            log.error("Greška pri kreiranju backupa: {}", e.getMessage(), e);
        }
    }

    public void restoreBackup() {
        try {
            userRepository.restoreFromBackup();
            System.out.println("Backup uspješno vraćen");
            log.info("Backup uspješno vraćen");
        } catch (Exception e) {
            System.out.println("Greška pri vraćanju backupa: " + e.getMessage());
            log.error("Greška pri vraćanju backupa: {}", e.getMessage(), e);
        }
    }

}
