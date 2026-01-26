package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.event.EventCategory;
import hr.projekt.todoapplication.model.event.PriorityLevel;
import hr.projekt.todoapplication.repository.EventRepository;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.DialogUtil;
import hr.projekt.todoapplication.util.MenuLoader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class EventAddController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateField;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ComboBox<EventCategory> categoryCombo;
    @FXML private ComboBox<PriorityLevel> priorityCombo;
    @FXML private VBox menuContainer;

    private final EventRepository eventRepository = EventRepository.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();

    private Event eventToEdit;
    private Runnable onEventChanged;

    public void setOnEventChanged(Runnable callback) {
        this.onEventChanged = callback;
    }

    @FXML
    public void initialize() {
        MenuLoader.loadMenuForCurrentUser(menuContainer);
        setupDatePickerConfig();
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        createTimeSpinnerFactory(hourFactory, hourSpinner);

        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        createTimeSpinnerFactory(minuteFactory, minuteSpinner);

        categoryCombo.setItems(FXCollections.observableArrayList(EventCategory.values()));
        categoryCombo.setValue(EventCategory.OSNOVNO);
        priorityCombo.setItems(FXCollections.observableArrayList(PriorityLevel.values()));
        priorityCombo.setValue(PriorityLevel.ZADANO);
    }

    public void getDataForUpdate(Event event) {
        this.eventToEdit = event;
        titleField.setText(event.getTitle());
        descriptionField.setText(event.getDescription());
        dateField.setValue(event.getDueDate().toLocalDate());
        hourSpinner.getValueFactory().setValue(event.getDueDate().getHour());
        minuteSpinner.getValueFactory().setValue(event.getDueDate().getMinute());
        categoryCombo.setValue(event.getInfo().category());
        priorityCombo.setValue(event.getInfo().priority());
    }

    @FXML
    private void createEvent() {
        if (!validateInput()) {
            return;
        }
        LocalDateTime date = getDateTimeFromInputs();

        userRepository.getCurrentUser().ifPresentOrElse(
                user -> {
                    Event.EventBuilder builder = new Event.EventBuilder(
                            titleField.getText().trim(),
                            descriptionField.getText().trim(),
                            date,
                            user.getId())
                            .category(categoryCombo.getValue())
                            .priority(priorityCombo.getValue());
                    if (eventToEdit != null) {
                        builder.id(eventToEdit.getId());
                        Event updatedEvent = builder.build();
                        eventRepository.updateEvent(updatedEvent);
                        DialogUtil.showInfo("Događaj uspješno ažuriran!");

                        if (onEventChanged != null) {
                            onEventChanged.run();
                        }
                        eventToEdit = null;
                    } else {
                        Event newEvent = builder.build();
                        eventRepository.saveEvent(newEvent);
                        DialogUtil.showInfo("Događaj uspješno kreiran i spremljen!");
                    }
                    clearFields();
                },
                () -> DialogUtil.showError("Nema prijavljenog korisnika")
        );
    }

    private boolean validateInput() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            DialogUtil.showError("Naslov ne smije biti prazan!");
            return false;
        }

        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            DialogUtil.showError("Opis ne smije biti prazan!");
            return false;
        }

        Optional<LocalDate> dueDate = Optional.ofNullable(dateField.getValue());
        if (dueDate.isEmpty()) {
            DialogUtil.showError("Morate odabrati datum!");
            return false;
        }

        LocalDateTime date = getDateTimeFromInputs();
        if (date.isBefore(LocalDateTime.now())) {
            DialogUtil.showError("Datum događaja ne može biti u prošlosti!");
            return false;
        }
        return true;
    }

    private LocalDateTime getDateTimeFromInputs() {
        LocalDate date = dateField.getValue();
        LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
        return LocalDateTime.of(date, time);
    }

    private void setupDatePickerConfig() {
        dateField.setEditable(false);
        String pattern = "dd.MM.yyyy.";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        dateField.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return Optional.ofNullable(date)
                        .map(formatter::format)
                        .orElse("");
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isBlank()) {
                    return LocalDate.now();
                }
                try {
                    return LocalDate.parse(string, formatter);
                } catch (DateTimeParseException _) {
                    return LocalDate.now();
                }
            }
        });
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
                return Optional.ofNullable(value)
                        .map(v -> String.format("%02d", v))
                        .orElse("00");
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
