package hr.projekt.todoapplication.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private PasswordUtil() {
        /*
        ovo je za hashiranje lozinke
         */
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashPassword) {
        return BCrypt.checkpw(plainPassword, hashPassword);
    }
}
