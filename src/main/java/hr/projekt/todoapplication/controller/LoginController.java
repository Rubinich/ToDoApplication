package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.ToDoApplication;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        if(username.isEmpty() || password.isEmpty()) {
            DialogUtil.showError("Molimo unesite korisničko ime i lozinku.");
            return;
        }

        Optional<User> userOpt = userRepository.authenticate(username, password);
        if(userOpt.isPresent()) {
            ToDoApplication.showMainScreen();
        } else {
            DialogUtil.showError("Pogrešno korisničko ime ili lozinka!");
        }
    }
}
