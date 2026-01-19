package hr.projekt.todoapplication.model.user;

import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Predstavlja osnovnu apstraktnu klasu korisnika u sustavu.
 * <p>
 *     Klasa sadrzi zajednicke podatke i metode koje dijele svi korisnici aplikacije,
 *     poput korisnickog imena, lozinke i liste dogadaja.
 *     Nasljeduju je konkretne klase poput {@link RegularUser} i {@link AdminUser}.
 * </p>
 */
@JsonbTypeInfo(
        key = "@type",
        value = {
                @JsonbSubtype(alias = "admin", type = AdminUser.class),
                @JsonbSubtype(alias = "regular", type = RegularUser.class)
        }
)
public abstract class User implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(User.class);

    protected String id;
    protected String username;
    protected String password;
    @JsonbTransient
    protected UserType userType;

    protected User() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Stvara novog korisnika s definiranim korisnickim imenom i lozinkom.
     *
     * @param username korisnicko ime
     */
    protected User(String username) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.userType = this instanceof AdminUser ? UserType.ADMIN : UserType.USER;
        log.debug("Kreiran novi korisnik: {}", username);
    }
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Vraca korisnicko ime.
     *
     * @return korisnicko ime
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    /**
     * Hash baziran na korisničkom imenu.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public String getId() {
        return id;
    }
}