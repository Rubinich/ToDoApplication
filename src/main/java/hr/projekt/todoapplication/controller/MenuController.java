package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.ToDoApplication;
import hr.projekt.todoapplication.exceptions.MenuLoadingException;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.DatabaseBackupUtil;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    private final UserRepository userRepository = UserRepository.getInstance();

    public void openHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("main-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            Stage stage = ToDoApplication.getInstance().getMainStage();
            stage.setTitle("Početak");
            stage.setScene(scene);
            stage.show();

        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void openEventSearch() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("event/event-search-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            Stage stage = ToDoApplication.getInstance().getMainStage();
            stage.setTitle("Pretraga događaja");
            stage.setScene(scene);
            stage.show();

        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void openAddEvent() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("event/event-add-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            Stage stage = ToDoApplication.getInstance().getMainStage();
            stage.setTitle("Dodavanje događaja");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logoutEvent() {
        try{
            boolean confirmed = DialogUtil.showConfirm("Jeste li sigurni da se želite odjaviti?");
            if(!confirmed)
                return;

            userRepository.logout();
            FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("login-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            Stage stage = ToDoApplication.getInstance().getMainStage();
            stage.setTitle("Prijava");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new MenuLoadingException(e);
        }
    }

    public void backupUsers() {
        boolean confirmed = DialogUtil.showConfirm("""
                        Kreirati backup tablice USERS?
                        
                        Backup: USERS_BACKUP""");

        if (confirmed) {
            DatabaseBackupUtil.createBackup("USERS");
            DialogUtil.showInfo("Backup pokrenut! Provjerite log za rezultat.");
        }
    }

    public void backupEvents() {
        boolean confirmed = DialogUtil.showConfirm("""
                Kreirati backup tablice EVENTS?
                
                Backup: EVENTS_BACKUP""");

        if (confirmed) {
            DatabaseBackupUtil.createBackup("EVENTS");
            DialogUtil.showInfo("Backup pokrenut! Provjerite log za rezultat.");
        }
    }
}
