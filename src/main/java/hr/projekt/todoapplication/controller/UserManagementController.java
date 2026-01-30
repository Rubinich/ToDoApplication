package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.user.AdminUser;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.EventRepository;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.DialogUtil;
import hr.projekt.todoapplication.util.MenuLoader;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Set;

public class UserManagementController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> columnUsername;
    @FXML private TableColumn<User, String> columnUserType;
    @FXML private TableColumn<User, String> columnEventCount;
    @FXML private TableColumn<User, Void> columnActions;
    @FXML private VBox menuContainer;

    private final UserRepository userRepository = UserRepository.getInstance();
    private final EventRepository eventRepository = EventRepository.getInstance();
    private ObservableList<User> users;

    @FXML
    public void initialize() {
        MenuLoader.loadMenuForCurrentUser(menuContainer);
        setTableColumns();
        loadUsers();
    }

    private void setTableColumns() {
        columnUsername.setCellValueFactory(value -> new ReadOnlyStringWrapper(value.getValue().getUsername()));
        columnUserType.setCellValueFactory(value -> new ReadOnlyStringWrapper(value.getValue().getUserType().getType()));
        columnEventCount.setCellValueFactory(value -> {
            int count = eventRepository.getEventCountForUser(value.getValue().getId());
            return new ReadOnlyStringWrapper(String.valueOf(count));
        });
        columnActions.setCellFactory(value -> new TableCell<>(){
            private final Button deleteButton = new Button("Izbriši");
            private final HBox container = new HBox(deleteButton);

            {
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    Thread.startVirtualThread(() -> {
                        try {
                            eventRepository.deleteAllEventsForUser(user.getId());
                            userRepository.deleteUser(user.getId());
                            Platform.runLater(() -> {
                                users.remove(user);
                                DialogUtil.showInfo("Korisnik i njegovi događaji uspješno izbrisani.");
                            });
                        } catch (Exception _) {
                            Platform.runLater(() -> DialogUtil.showError("Dogodila se greška prilikom brisanja korisnika."));
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user instanceof AdminUser || user.equals(userRepository.getCurrentUser())) {
                        deleteButton.setDisable(true);
                        deleteButton.setText("Zaštićeno");
                    } else {
                        deleteButton.setDisable(false);
                        deleteButton.setText("Izbriši");
                    }
                    setGraphic(container);
                }
            }
        });
    }

    public void loadUsers() {
        Thread.startVirtualThread(() -> {
            try{
                Set<User> userSet = userRepository.getAllUsers();
                Platform.runLater(() -> {
                    users = FXCollections.observableArrayList(userSet);
                    userTable.setItems(users);
                });
            } catch (Exception _) {
                Platform.runLater(() ->
                        DialogUtil.showError("Greška pri učitavanju korisnika")
                );
            }
        });
    }
}
