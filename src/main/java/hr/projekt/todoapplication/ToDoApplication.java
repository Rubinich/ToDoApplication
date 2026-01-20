package hr.projekt.todoapplication;

import hr.projekt.todoapplication.util.DatabaseUtil;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class ToDoApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ToDoApplication.class);
    private static Stage mainStage;

    public static Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;

//        if (!DatabaseUtil.testConnection()) {
//            DialogUtil.showError("""
//                    Molimo provjerite:
//
//                    1. Je li H2 server pokrenut?
//                       Pokrenite: java -jar h2*.jar
//                    2. Je li baza dostupna na:
//                       jdbc:h2:tcp://localhost/~/Java-2026
//                    3. Provjerite username i password u database.properties
//
//                    Aplikacija će se zatvoriti.""");
//            Platform.exit();
//            System.exit(1);
//        }

        FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        mainStage.setTitle("ToDo Application!");
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void showMainScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ToDoApplication.class.getResource("main-screen.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            mainStage.setTitle("ToDo Application!");
            mainStage.setScene(scene);
            mainStage.show();

        } catch (IOException e) {
            logger.error("Greška pri učitavanju glavnog ekrana", e);
            DialogUtil.showError("Greška pri učitavanju glavnog ekrana");
        }
    }
}
