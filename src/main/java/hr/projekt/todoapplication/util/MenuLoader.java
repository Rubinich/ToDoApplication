package hr.projekt.todoapplication.util;

import hr.projekt.todoapplication.exceptions.MenuLoadingException;
import hr.projekt.todoapplication.model.user.AdminUser;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.UserRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Optional;

public class MenuLoader {
    private static final Logger logger = LoggerFactory.getLogger(MenuLoader.class);
    private static UserRepository userRepository = UserRepository.getInstance();

    private MenuLoader() {}

    public static void loadMenuForCurrentUser(Pane container) {
        String fxmlMenu = "";
        Optional<User> currentUser = userRepository.getCurrentUser();
        if(currentUser.isPresent())
            fxmlMenu = currentUser.get() instanceof AdminUser ? "/hr/projekt/todoapplication/menu/admin-menu.fxml" : "/hr/projekt/todoapplication/menu/user-menu.fxml";

        try{
            FXMLLoader loader = new FXMLLoader(MenuLoader.class.getResource(fxmlMenu));
            Parent menu = loader.load();
            container.getChildren().setAll(menu);
            logger.debug("Učitani meni: {}", fxmlMenu);
        } catch (IOException e) {
            throw new MenuLoadingException(e);
        }
    }
}
