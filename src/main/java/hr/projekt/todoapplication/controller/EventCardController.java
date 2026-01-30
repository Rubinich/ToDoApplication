package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.ToDoApplication;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.repository.EventRepository;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventCardController {
    private static final Logger logger = LoggerFactory.getLogger(EventCardController.class);
    @FXML private VBox cardContainer;
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private ContextMenu contextMenu;
    private EventRepository eventRepository = EventRepository.getInstance();
    private Event event;
    private Runnable onEventChanged;

    public void setEvent(Event event) {
        this.event = event;
        Optional.ofNullable(event).ifPresent(e -> {
            titleLabel.setText(e.getTitle());
            dateLabel.setText(DATE_FORMATTER.format(e.getDueDate()));
            setupContextMenu();
        });
    }

    public void setOnEventChanged(Runnable callback) {
        this.onEventChanged = callback;
    }

    private void setupContextMenu() {
        contextMenu = new ContextMenu();
        MenuItem detailItem = new MenuItem("Prikaži detalje");
        //detailItem.setOnAction(_ -> handleDetails());
        MenuItem editItem = new MenuItem("Uredi");
        editItem.setOnAction(_ -> handleEdit());
        MenuItem deleteItem = new MenuItem("Izbriši");
        deleteItem.setOnAction(_ -> handleDelete());

        contextMenu.getItems().addAll(detailItem, editItem, deleteItem);
        cardContainer.setOnContextMenuRequested(event -> {
            if (contextMenu.isShowing()) contextMenu.hide();
            contextMenu.show(cardContainer.getScene().getWindow(), event.getScreenX(), event.getScreenY());
            event.consume();
        });

        cardContainer.setOnMouseEntered(event ->
            cardContainer.setStyle(
                    "-fx-padding: 10; " +
                            "-fx-border-color: #2196f3; " +
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-color: #e3f2fd;"
            )
        );
        cardContainer.setOnMouseExited(event ->
            cardContainer.setStyle(
                    "-fx-padding: 10; " +
                            "-fx-border-color: grey; " +
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand;"
            )
        );
    }

    private void handleDelete() {
        if (contextMenu != null && contextMenu.isShowing()) contextMenu.hide();

        boolean confirmed = DialogUtil.showConfirm("Jeste li sigurni da želite izbrisati ovaj događaj?");
        if (!confirmed) {
            logger.info("Brisanje događaja otkazano od strane korisnika");
            return;
        }

        Thread.ofVirtual().start(() -> {
            try{
                eventRepository.deleteEventFromEverywhere(event.getId());
                Platform.runLater(() -> {
                    if (onEventChanged != null) onEventChanged.run();
                });

            } catch (Exception e) {
                logger.error("Greška pri brisanju: {}", e.getMessage());
                Platform.runLater(() -> {
                    DialogUtil.showError("Greška pri brisanju: " + e.getMessage());
                });
            }
        });
    }

    private void handleEdit() {
        if (contextMenu != null && contextMenu.isShowing()) contextMenu.hide();

        try{
            FXMLLoader loader = new FXMLLoader(ToDoApplication.class.getResource("event/event-add-screen.fxml"));
            Scene scene = new Scene(loader.load(), 1024, 768);
            EventAddController controller = loader.getController();
            controller.setOnEventChanged(onEventChanged);
            controller.getDataForUpdate(this.event);

            Stage stage = ToDoApplication.getInstance().getMainStage();
            stage.setTitle("Uređivanje događaja");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            logger.error("Greška pri uređivanju događaja: {}", e.getMessage());
            Platform.runLater(() -> {
                DialogUtil.showError("Greška pri uređivanju događaja: " + e.getMessage());
            });
        }
    }
}
