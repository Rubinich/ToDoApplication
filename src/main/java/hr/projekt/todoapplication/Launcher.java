package hr.projekt.todoapplication;

import javafx.application.Application;

public class Launcher {
    private Launcher() {}
    static void main(String[] args) {
        Application.launch(ToDoApplication.class, args);
    }
}
