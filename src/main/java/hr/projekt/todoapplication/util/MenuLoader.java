package hr.projekt.todoapplication.util;

import hr.projekt.todoapplication.ToDoApplication;
import hr.projekt.todoapplication.model.user.AdminUser;
import hr.projekt.todoapplication.model.user.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Optional;

public class MenuLoader {
    private static final Logger logger = LoggerFactory.getLogger(MenuLoader.class);

    public static void loadMenuForCurrentUser(Pane container) {
        Optional<User> currentUser = ToDoApplication.getCurrentUser();
        if(currentUser.isEmpty()) {
            logger.warn("No user logged in, cannot load menu");
            return;
        }

        String fxmlMenu = currentUser.get() instanceof AdminUser ? "/hr/projekt/todoapplication/menu/admin-menu.fxml" : "/hr/projekt/todoapplication/menu/user-menu.fxml";
        try{
            FXMLLoader loader = new FXMLLoader(MenuLoader.class.getResource(fxmlMenu));
            Parent menu = loader.load();
            container.getChildren().setAll(menu);
            logger.debug("Učitani meni: {}", fxmlMenu);
        } catch (IOException e) {
            logger.error("Error loading menu: {}", fxmlMenu, e);
            throw new RuntimeException(e);
        }
    }
}
