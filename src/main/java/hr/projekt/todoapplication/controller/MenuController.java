package hr.projekt.todoapplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import java.util.Arrays;
import java.util.List;

public class MenuController {

    @FXML
    private Menu searchMenu;
    @FXML
    private Menu filterMenu;
    @FXML
    private Menu sortMenu;
    @FXML
    private Menu groupMenu;

    @FXML
    public void initialize() {
        List<String> options = Arrays.asList(
                "Po korisničkom imenu",
                "Po naslovu",
                "Po opisu",
                "Po datumu",
                "Po prioritetu",
                "Po kategoriji"
        );

        fillTheMenu(searchMenu, options);
        fillTheMenu(filterMenu, options);
        fillTheMenu(sortMenu, options);
        fillTheMenu(groupMenu, options);
    }

    private void fillTheMenu(Menu menu, List<String> options) {
        for(String option : options) {
            MenuItem item = new MenuItem(option);
            item.setOnAction(e -> callSearchOption(option));
            menu.getItems().add(item);
        }
    }

    private void callSearchOption(String option) {

    }

    private void searchByCategory() {
    }

    private void searchByPriority() {
    }

    private void searchByDate() {
    }

    private void searchByDescription() {
    }

    private void searchByTitle() {
    }

}
