package hr.projekt.todoapplication.model.user;

import hr.projekt.todoapplication.model.Planner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Predstavlja obicnog korisnika aplikacije.
 * <p>
 *     Regularni korisnik moze kreirati dogadaje i pregledavati
 *     vlastite dogadaje, ali nema administratorske ovlasti.
 * </p>
 */
public class RegularUser extends User {
    private static final Logger log = LoggerFactory.getLogger(RegularUser.class);
    public RegularUser() {
        super();
    }

    /**
     * Stvara novog obicnog korisnika s definiranim korisnickim imenom i lozinkom.
     *
     * @param username korisnicko ime
     */
    public RegularUser(String username) {
        super(username);
    }
}
