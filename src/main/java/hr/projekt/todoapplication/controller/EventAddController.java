package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.event.EventCategory;
import hr.projekt.todoapplication.model.event.PriorityLevel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class EventAddController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateField;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ComboBox<EventCategory> categoryCombo;
    @FXML private ComboBox<PriorityLevel> priorityCombo;
    @FXML private Button addButton;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        createTimeSpinnerFactory(hourFactory, hourSpinner);

        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        createTimeSpinnerFactory(minuteFactory, minuteSpinner);

        categoryCombo.setItems(FXCollections.observableArrayList(EventCategory.values()));
        categoryCombo.setValue(EventCategory.OSNOVNO);
        priorityCombo.setItems(FXCollections.observableArrayList(PriorityLevel.values()));
        priorityCombo.setValue(PriorityLevel.ZADANO);
    }

    private void createTimeSpinnerFactory(SpinnerValueFactory<Integer> factory, Spinner<Integer> spinner) {
        factory.setWrapAround(true);
        spinner.setValueFactory(factory);
        spinner.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if(code == KeyCode.W) {
                spinner.increment();
            } else if(code == KeyCode.S) {
                spinner.decrement();
            }
        });
    }

}
