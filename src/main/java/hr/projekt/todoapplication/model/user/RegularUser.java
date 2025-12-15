package hr.projekt.todoapplication.model.user;

/**
 * Predstavlja obicnog korisnika aplikacije.
 * <p>
 *     Regularni korisnik moze kreirati dogadaje i pregledavati
 *     vlastite dogadaje, ali nema administratorske ovlasti.
 * </p>
 */
public class RegularUser extends User {
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
