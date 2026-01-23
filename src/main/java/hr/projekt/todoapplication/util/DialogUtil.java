package hr.projekt.todoapplication.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class DialogUtil {

    private DialogUtil() {}

    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacija");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static boolean showConfirm(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        ButtonType daButton = new ButtonType("Da");
        ButtonType neButton = new ButtonType("Ne");
        alert.getButtonTypes().setAll(daButton, neButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == daButton;
    }
}
