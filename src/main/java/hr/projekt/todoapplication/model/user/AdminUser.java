package hr.projekt.todoapplication.model.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Omogucuje administrativne funkcionalnosti unutar aplikacije.
 * <p>
 *     Klasa nasljeduje {@link User} i prosiruje njegove mogucnosti, dodajuci
 *     administratorske opcije poput pregleda statistike sustava, brisanja i blokiranja
 *     korisnika te sortiranja dogadaja.
 * </p>
 * <p>
 *     Administrator ima pristup svim standardnim funkcijama korisnika, uz dodatne
 *     privilegije za kontrolu i upravljanje korisnickim racunima.
 * </p>
 */
public class AdminUser extends User {
    private static final Logger log = LoggerFactory.getLogger(AdminUser.class);
    public AdminUser() {
        super();
    }
    /**
     * Stvara novog administratora s definiranim korisnickim imenom i lozinkom.
     *
     * @param username korisnicko ime administratora
     */
    public AdminUser(String username) {
        super(username);
    }

    /**
     * Ispisuje statistiku o korisnicima sustava koristeci Stream API.
     * <p>Prikazuje:
     *     <ul>
     *         <li>Sve korisnike s njihovom ulogom i brojem dogadaja</li>
     *         <li>Korisnike s najmanje i najvise dogadaja</li>
     *         <li>Podjelu korisnika na administratore i obicne korisnike</li>
     *     </ul>
     * </p>
     *
     * @param users kolekcija korisnika ciji se podaci analiziraju
     */
    private void printStats(Collection<? extends User> users) {
        if (users.isEmpty()) {
            log.info("Administrator {} pokusao generirati statistiku, ali nema korisnika.", username);
            System.out.println("Nema registriranih korisnika.");
            return;
        }

        List<? extends User> safeList = users.stream()
                .filter(Objects::nonNull)
                .toList();
        List<User> userList = new ArrayList<>(safeList);

        if (userList.isEmpty()) {
            System.out.println("Nema registriranih korisnika.");
            return;
        }

        System.out.println("\n==== PREGLED KORISNIKA ====");
        userList.forEach(u -> System.out.printf("- %s [%s] -> %d dogadaj(a)%n",
                u.getUsername(),
                (u instanceof AdminUser ? "Administrator" : "Korisnik"),
                u.getEventCount()));

        Map<Integer, List<User>> grouped = userList.stream()
                .collect(Collectors.groupingBy(User::getEventCount));

        int minCount = Collections.min(grouped.keySet());
        int maxCount = Collections.max(grouped.keySet());

        System.out.println("\n==== STATISTIKA DOGADAJA ====");
        System.out.println("Korisnik(i) s najmanje dogadaja (" + minCount + "):");
        grouped.get(minCount).forEach(u ->
                System.out.printf("- %s [%s]%n", u.getUsername(),
                        (u instanceof AdminUser ? "Administrator" : "Korisnik")));

        if (maxCount != minCount) {
            System.out.println("\nKorisnik(i) s najvise dogadaja (" + maxCount + "):");
            grouped.get(maxCount).forEach(u ->
                    System.out.printf("- %s [%s]%n", u.getUsername(),
                            (u instanceof AdminUser ? "Administrator" : "Korisnik")));
        }

        Map<Boolean, List<User>> partitioned = userList.stream()
                .collect(Collectors.partitioningBy(AdminUser.class::isInstance));

        System.out.println("\n==== ADMINISTRATORI ====");
        partitioned.get(true).forEach(u ->
                System.out.printf("- %s -> %d dogadaj(a)%n", u.getUsername(), u.getEventCount()));

        System.out.println("\n==== KORISNICI ====");
        partitioned.get(false).forEach(u ->
                System.out.printf("- %s -> %d dogadaj(a)%n", u.getUsername(), u.getEventCount()));

        log.info("Administrator {} prikazao statistiku korisnika i dogadaja.", username);
    }
}
