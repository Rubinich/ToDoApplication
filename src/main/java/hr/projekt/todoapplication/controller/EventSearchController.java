package hr.projekt.todoapplication.controller;

import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.event.SearchCriteria;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.repository.EventRepository;
import hr.projekt.todoapplication.repository.UserRepository;
import hr.projekt.todoapplication.util.DialogUtil;
import hr.projekt.todoapplication.util.MenuLoader;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class EventSearchController{

    @FXML private ComboBox<SearchCriteria> comboCriteria;
    @FXML private TextField searchField;
    @FXML private TableView<Event> table;
    @FXML private TableColumn<Event, String> columnUser;
    @FXML private TableColumn<Event, String> columnTitle;
    @FXML private TableColumn<Event, String> columnDesc;
    @FXML private TableColumn<Event, String> columnDate;
    @FXML private VBox menuContainer;

    private FilteredList<Event> filteredEvents;
    private String currentUsername = "";

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        UserRepository userRepository = UserRepository.getInstance();
        EventRepository eventRepository = EventRepository.getInstance();
        MenuLoader.loadMenuForCurrentUser(menuContainer);
        Optional<User> currentUser = userRepository.getCurrentUser();
        currentUsername = currentUser.map(User::getUsername).orElse("Nepoznat");

        columnUser.setCellValueFactory(_ -> new ReadOnlyStringWrapper(currentUsername));
        columnTitle.setCellValueFactory(param -> new ReadOnlyStringWrapper(Optional.ofNullable(param.getValue().getTitle()).orElse("")));
        columnDesc.setCellValueFactory(param -> new ReadOnlyStringWrapper(Optional.ofNullable(param.getValue().getDescription()).orElse("")));
        columnDate.setCellValueFactory(param -> new ReadOnlyStringWrapper(Optional.ofNullable(param.getValue().getDueDate()).map(DTF::format).orElse("")));

        filteredEvents = new FilteredList<>(FXCollections.observableArrayList(eventRepository.getCachedEvents()));
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
        return Optional.ofNullable(event.getTitle())
                .map(String::toLowerCase)
                .map(title -> title.contains(searchLower))
                .orElse(false);
    }

    private boolean matchDescription(Event event, String searchLower) {
        List<String> tokens = Arrays.stream(searchLower.split("\\s+"))
            .filter(t -> t.length() > 2)
            .toList();
        return Optional.ofNullable(event.getDescription())
                .filter(description -> tokens.isEmpty() || tokens.stream().allMatch(description.toLowerCase()::contains))
                .isPresent();
    }

    private boolean matchDateTime(LocalDateTime dateTime, String input) {
        return Optional.ofNullable(dateTime)
                .map(dt -> tryParseDateTime(dt, input))
                .orElse(false);
    }

    private boolean tryParseDateTime(LocalDateTime dateTime, String input) {
        if(tryParseAsDateTime(dateTime, input))
            return true;
        if(tryParseAsDate(dateTime, input))
            return true;
        return tryParseAsTime(dateTime, input);
    }

    private boolean tryParseAsTime(LocalDateTime dateTime, String input) {
        try {
            LocalTime parsed = LocalTime.parse(input, DTF);
            return dateTime.toLocalTime().equals(parsed);
        } catch (DateTimeParseException _) {
            return false;
        }
    }

    private boolean tryParseAsDate(LocalDateTime dateTime, String input) {
        try {
            LocalDate parsed = LocalDate.parse(input, DF);
            return dateTime.toLocalDate().equals(parsed);
        } catch (DateTimeParseException _) {
            return false;
        }
    }

    private boolean tryParseAsDateTime(LocalDateTime dateTime, String input) {
        try {
            LocalDateTime parsed = LocalDateTime.parse(input, TF);
            return dateTime.equals(parsed);
        } catch (DateTimeParseException _) {
            return false;
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        comboCriteria.setValue(SearchCriteria.TITLE);
        filteredEvents.setPredicate(_ -> true);
    }
}