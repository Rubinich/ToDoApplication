package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventCardController {
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public void setEvent(Event event) {
        Optional.ofNullable(event).ifPresent(e -> {
            titleLabel.setText(e.getTitle());
            dateLabel.setText(DATE_FORMATTER.format(e.getDueDate()));
        });
    }
}
