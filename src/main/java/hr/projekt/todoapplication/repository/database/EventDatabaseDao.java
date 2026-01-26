package hr.projekt.todoapplication.repository.database;

import hr.projekt.todoapplication.exceptions.DatabaseException;
import hr.projekt.todoapplication.model.event.Event;
import hr.projekt.todoapplication.model.event.EventCategory;
import hr.projekt.todoapplication.model.event.PriorityLevel;
import hr.projekt.todoapplication.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDatabaseDao implements EventDao{
    private static final Logger logger = LoggerFactory.getLogger(EventDatabaseDao.class);
    private static final String INSERT_EVENT_QUERY = "INSERT INTO EVENTS (ID, TITLE, DESCRIPTION, DUE_DATE, PRIORITY, CATEGORY, USER_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_EVENTS_BY_USER_ID_QUERY = "SELECT ID, TITLE, DESCRIPTION, DUE_DATE, PRIORITY, CATEGORY, USER_ID FROM EVENTS WHERE USER_ID = ? ORDER BY DUE_DATE";
    private static final String DELETE_EVENT_QUERY = "DELETE FROM EVENTS WHERE ID = ?";
    private static final String UPDATE_EVENT_QUERY = "UPDATE EVENTS SET TITLE = ?, DESCRIPTION = ?, DUE_DATE = ?, PRIORITY = ?, CATEGORY = ? WHERE ID = ?";

    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_TITLE = "TITLE";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_DUE_DATE = "DUE_DATE";
    private static final String COLUMN_PRIORITY = "PRIORITY";
    private static final String COLUMN_CATEGORY = "CATEGORY";
    private static final String COLUMN_USER_ID = "USER_ID";

    @Override
    public void save(Event event) {
        try(Connection conn = DatabaseUtil.createConnection();
            PreparedStatement statement = conn.prepareStatement(INSERT_EVENT_QUERY)) {

            statement.setString(1, event.getId());
            statement.setString(2, event.getTitle());
            statement.setString(3, event.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(event.getDueDate()));
            statement.setString(5, event.getInfo().priority().name());
            statement.setString(6, event.getInfo().category().name());
            statement.setString(7, event.getUserId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<Event> findByUserId(String userId)  throws IOException {
        List<Event> events = new ArrayList<>();

        try (Connection conn = DatabaseUtil.createConnection();
             PreparedStatement statement = conn.prepareStatement(SELECT_EVENTS_BY_USER_ID_QUERY)) {

            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String id = rs.getString(COLUMN_ID);
                String title = rs.getString(COLUMN_TITLE);
                String description = rs.getString(COLUMN_DESCRIPTION);
                LocalDateTime dueDate = rs.getTimestamp(COLUMN_DUE_DATE).toLocalDateTime();
                PriorityLevel priority = PriorityLevel.valueOf(rs.getString(COLUMN_PRIORITY));
                EventCategory category = EventCategory.valueOf(rs.getString(COLUMN_CATEGORY));
                String eventUserId  = rs.getString(COLUMN_USER_ID);

                Event event = new Event.EventBuilder(title, description, dueDate, eventUserId)
                        .priority(priority)
                        .category(category)
                        .id(id)
                        .build();
                events.add(event);
            }
        } catch (SQLException e) {
            logger.error("Greška pri učitavanju događaja: {}", e.getMessage());
        }
        return events;
    }

    @Override
    public List<Event> findAll() {
        return List.of();
    }

    @Override
    public void update(Event event) {
        try(Connection conn = DatabaseUtil.createConnection();
            PreparedStatement statement = conn.prepareStatement(UPDATE_EVENT_QUERY);) {
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(event.getDueDate()));
            statement.setString(4, event.getInfo().priority().name());
            statement.setString(5, event.getInfo().category().name());
            statement.setString(6, event.getId());
            statement.executeUpdate();

        } catch(SQLException e) {
            logger.error("Greška pri ažuriranju događaja: {}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    @Override
    public void delete(String eventId) {
        try(Connection conn = DatabaseUtil.createConnection();
            PreparedStatement statement = conn.prepareStatement(DELETE_EVENT_QUERY);) {

            statement.setString(1, eventId);
            statement.executeUpdate();

        } catch(SQLException e) {
            logger.error("Greška pri brisanju događaja: {}", e.getMessage());
            throw new DatabaseException(e);
        }
    }
}
