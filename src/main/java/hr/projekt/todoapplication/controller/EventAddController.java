package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.Planner;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.event.EventCategory;
import hr.projekt.todoapplication.model.event.PriorityLevel;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class EventAddController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateField;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ComboBox<EventCategory> categoryCombo;
    @FXML private ComboBox<PriorityLevel> priorityCombo;
    private Planner planner;

    @FXML
    public void initialize() {
        planner = new Planner();
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
        factory.setConverter(twoDigitConverter());
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

    @FXML
    private void createEvent() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            DialogUtil.showError("Naslov ne smije biti prazan!");
            return;
        }
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            DialogUtil.showError("Opis ne smije biti prazan!");
            return;
        }
        Optional<LocalDate> dueDate = Optional.ofNullable(dateField.getValue());
        if(dueDate.isEmpty()) {
            DialogUtil.showError("Morate odabrati datum!");
            return;
        }
        LocalDateTime date = LocalDateTime.of(dueDate.get(), LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
        if(date.isBefore(LocalDateTime.now())) {
            DialogUtil.showError("Datum događaja ne može biti u prošlosti!");
            return;
        }

        Event newEvent = new Event.EventBuilder(title, description, date)
                .category(categoryCombo.getValue())
                .priority(priorityCombo.getValue())
                .build();
        try {
            planner.addEventToCurrentUser(newEvent);
            DialogUtil.showInfo("Događaj uspješno kreiran i spremljen!");
            clearFields();
        } catch (IllegalStateException e) {
            DialogUtil.showError("Nema prijavljenog korisnika!");
        } catch (Exception e) {
            DialogUtil.showError("Greška pri dodavanju događaja: " + e.getMessage());
        }
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        dateField.setValue(null);
        hourSpinner.getValueFactory().setValue(12);
        minuteSpinner.getValueFactory().setValue(0);
        categoryCombo.setValue(EventCategory.OSNOVNO);
        priorityCombo.setValue(PriorityLevel.ZADANO);
    }

    private StringConverter<Integer> twoDigitConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "00" : String.format("%02d", value);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException _) {
                    return 0;
                }
            }
        };
    }
}
