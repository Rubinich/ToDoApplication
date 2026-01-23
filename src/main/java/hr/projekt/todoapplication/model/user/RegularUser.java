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
        this.userType = UserType.USER;
    }

    public RegularUser(String id, String username, String password, UserType userType) {
        super(id, username, password, userType);
    }

    public RegularUser(String username, String password) {
        super(username, password);
        this.userType = UserType.USER;
    }
}
