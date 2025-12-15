package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.Planner;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.event.SearchCriteria;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.util.DialogUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EventSearchController{

    @FXML private ComboBox<SearchCriteria> comboCriteria;
    @FXML private TextField searchField;
    @FXML private TableView<Event> table;
    @FXML private TableColumn<Event, String> columnUser;
    @FXML private TableColumn<Event, String> columnTitle;
    @FXML private TableColumn<Event, String> columnDesc;
    @FXML private TableColumn<Event, String> columnDate;

    private final Planner planner = new Planner();
    private FilteredList<Event> filteredEvents;
    private String currentUsername = "";

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        Optional<User> currentUser = planner.getCurrentUser();
        currentUsername = currentUser.map(User::getUsername).orElse("Nepoznat");

        columnUser.setCellValueFactory(_ -> new ReadOnlyStringWrapper(currentUsername));
        columnTitle.setCellValueFactory(param -> new ReadOnlyStringWrapper(Optional.ofNullable(param.getValue().getTitle()).orElse("")));
        columnDesc.setCellValueFactory(param -> new ReadOnlyStringWrapper(Optional.ofNullable(param.getValue().getDescription()).orElse("")));
        columnDate.setCellValueFactory(param -> new ReadOnlyStringWrapper(Optional.ofNullable(param.getValue().getDueDate()).map(DTF::format).orElse("")));

        List<Event> events = currentUser.map(User::getEvents).orElse(List.of());
        filteredEvents = new FilteredList<>(FXCollections.observableArrayList(events));
        table.setItems(filteredEvents);
        table.setPlaceholder(new Label(""));

        comboCriteria.setItems(FXCollections.observableArrayList(SearchCriteria.values()));
        comboCriteria.setValue(SearchCriteria.TITLE);
        comboCriteria.valueProperty().addListener((_, _, _) -> updatePrompt());
        updatePrompt();
    }

    private void updatePrompt() {
        SearchCriteria criteria = Optional.ofNullable(comboCriteria.getValue()).orElse(SearchCriteria.TITLE);
        searchField.setPromptText(switch (criteria) {
            case USERNAME -> "Upiši dio korisničkog imena...";
            case TITLE -> "Upiši dio naslova...";
            case DESCRIPTION -> "Upiši riječ/riječi iz opisa...";
            case DATETIME -> "dd.MM.yyyy. ili HH:mm ili dd.MM.yyyy. HH:mm";
        });
    }

    @FXML
    private void applySearch() {
        Optional<String> input = Optional.ofNullable(searchField.getText()).map(String::trim).filter(s -> !s.isBlank());
        if(input.isEmpty()) {
            filteredEvents.setPredicate(_ -> true);
            return;
        }

        String searchInput = input.get();
        String searchLower = input.get().toLowerCase();
        SearchCriteria criteria = Optional.ofNullable(comboCriteria.getValue()).orElse(SearchCriteria.TITLE);

        filteredEvents.setPredicate(e -> switch(criteria) {
            case USERNAME -> currentUsername.contains(searchLower);
            case TITLE -> matchTitle(e, searchLower);
            case DESCRIPTION -> matchDescription(e, searchLower);
            case DATETIME -> matchDateTime(e.getDueDate(), searchInput);
        });

        if (filteredEvents.isEmpty()) {
            DialogUtil.showInfo("Nema rezultata za zadane kriterije pretraživanja.");
        }
    }

    private boolean matchTitle(Event event, String searchLower) {
        String title = event.getTitle();
        if (title == null)
            return false;
        return title.toLowerCase().contains(searchLower);
    }

    private boolean matchDescription(Event event, String searchLower) {
        String description = event.getDescription();
        if (description == null)
            return false;
        String descLower = description.toLowerCase();
        List<String> tokens = Arrays.stream(searchLower.split("\\s+"))
                .map(String::trim)
                .filter(t -> t.length() >= 2)
                .toList();

        return tokens.isEmpty() || tokens.stream().allMatch(descLower::contains);
    }

    private boolean matchDateTime(LocalDateTime dt, String input) {
        if (dt == null)
            return false;
        return tryParseDateTime(dt, input);
    }

    private boolean tryParseDateTime(LocalDateTime dt, String input) {
        // dd.MM.yyyy. HH:mm
        try {
            LocalDateTime parsed = LocalDateTime.parse(input, DTF);
            return dt.equals(parsed);
        } catch (DateTimeParseException _) {
            // dd.MM.yyyy.
            try {
                LocalDate parsed = LocalDate.parse(input, DF);
                return dt.toLocalDate().equals(parsed);
            } catch (DateTimeParseException _) {
                // HH:mm
                try {
                    LocalTime parsed = LocalTime.parse(input, TF);
                    return dt.toLocalTime().equals(parsed);
                } catch (DateTimeParseException _) {
                    return false;
                }
            }
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        comboCriteria.setValue(SearchCriteria.TITLE);
        filteredEvents.setPredicate(_ -> true);
    }
}