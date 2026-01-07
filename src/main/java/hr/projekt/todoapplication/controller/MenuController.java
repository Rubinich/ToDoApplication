package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.ToDoApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {

    public void openHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("main-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

            ToDoApplication.getMainStage().setTitle("Početak");
            ToDoApplication.getMainStage().setScene(scene);
            ToDoApplication.getMainStage().show();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void openEventSearch() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("event/event-search-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

            ToDoApplication.getMainStage().setTitle("Pretraga događaja");
            ToDoApplication.getMainStage().setScene(scene);
            ToDoApplication.getMainStage().show();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void openAddEvent() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("event/event-add-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

            ToDoApplication.getMainStage().setTitle("Dodavanje događaja");
            ToDoApplication.getMainStage().setScene(scene);
            ToDoApplication.getMainStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
