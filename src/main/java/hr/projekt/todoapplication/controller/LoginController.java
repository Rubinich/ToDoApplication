package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.ToDoApplication;
import hr.projekt.todoapplication.model.user.RegularUser;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.model.user.UserType;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private UserRepository userRepository;

    @FXML
    void initialize() {
        this.userRepository = UserRepository.getInstance();
        passwordField.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if(validateInput(username, password)) {
            try {
                Optional<User> userOpt = userRepository.authenticate(username, password);
                if (userOpt.isPresent()) {
                    ToDoApplication.showMainScreen();
                } else {
                    DialogUtil.showError("""
                            Korisnik s unesenim podacima nije pronađen!
                            
                            Molimo provjerite:
                            • Jeste li ispravno unijeli korisničko ime
                            • Jeste li ispravno unijeli lozinku
                            Ako ste novi korisnik, molimo registrirajte se.""");
                }
            } catch (IOException e) {
                logger.error("Greška prilikom autentifikacije: {}", e.getMessage(), e);
                DialogUtil.showError("Došlo je do greške prilikom prijave.\nMolimo pokušajte ponovno.");
            }
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (userRepository.saveUser(new RegularUser(username, password))) {
            DialogUtil.showInfo("Registracija uspješna!");
        } else {
            DialogUtil.showError("Registracija nije uspjela. Korisničko ime je zauzeto.");
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            DialogUtil.showError("Molimo unesite korisničko ime i lozinku.");
            return false;
        }
        return true;
    }
}
