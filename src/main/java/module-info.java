module hr.projekt.todoapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens hr.projekt.todoapplication to javafx.fxml;
    exports hr.projekt.todoapplication;
    exports hr.projekt.todoapplication.controller;
    opens hr.projekt.todoapplication.controller to javafx.fxml;
}