package hr.projekt.todoapplication;

import hr.projekt.todoapplication.controller.LoginController;
import hr.projekt.todoapplication.model.user.AdminUser;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class ToDoApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ToDoApplication.class);
    private static Stage mainStage;
    private static Optional<User> currentUser;

    public static Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        mainStage.setTitle("ToDo Application!");
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void setCurrentUser(User user) {
        currentUser = Optional.ofNullable(user);
    }

    public static Optional<User> getCurrentUser() {
        return currentUser;
    }

    public static void showMainScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ToDoApplication.class.getResource("main-screen.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            String title = currentUser.get() instanceof AdminUser ? "ToDo Application - Admin" : "ToDo Application - " + currentUser.get().getUsername();

            mainStage.setTitle(title);
            mainStage.setScene(scene);
            mainStage.show();

        } catch (IOException e) {
            DialogUtil.showError("Greška pri učitavanju glavnog ekrana");
        }
    }
}
