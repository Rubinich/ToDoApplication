package hr.projekt.todoapplication.repository.database;

import hr.projekt.todoapplication.exceptions.DatabaseException;
import hr.projekt.todoapplication.model.user.AdminUser;
import hr.projekt.todoapplication.model.user.RegularUser;
import hr.projekt.todoapplication.model.user.User;
import hr.projekt.todoapplication.model.user.UserType;
import hr.projekt.todoapplication.util.DatabaseUtil;
import hr.projekt.todoapplication.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDatabaseDao implements UserDao{
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseDao.class);
    private static final String INSERT_USER_QUERY = "INSERT INTO USERS (ID, USERNAME, PASSWORD, USER_TYPE) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE USERS SET USERNAME = ?, PASSWORD = ?, USER_TYPE = ? WHERE ID = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM USERS WHERE ID = ?";
    private static final String SELECT_ALL_USERS_QUERY = "SELECT ID, USERNAME, PASSWORD, USER_TYPE FROM USERS";
    private static final String SELECT_USERS_BY_USERNAME_PASSWORD = "SELECT ID, USERNAME, PASSWORD, USER_TYPE FROM USERS WHERE USERNAME = ?";

    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_USERNAME = "USERNAME";
    private static final String COLUMN_PASSWORD= "PASSWORD";
    private static final String COLUMN_USER_TYPE = "USER_TYPE";

    @Override
    public void save(User user) {
        try(Connection conn = DatabaseUtil.createConnection();
            PreparedStatement statement = conn.prepareStatement(INSERT_USER_QUERY)) {

            statement.setString(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getUserType().getType());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        try(Connection conn = DatabaseUtil.createConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_USERS_BY_USERNAME_PASSWORD)){
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                User user = mapResultSetToUser(rs);
                if (PasswordUtil.checkPassword(password, user.getPassword()))
                    return Optional.of(user);
            }
            logger.warn("Autentifikacija neuspješna za: {}", username);

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Set<User> findAll(){
        Set<User> users = new HashSet<>();

        try (Connection conn = DatabaseUtil.createConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS_QUERY)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            logger.error("Greška pri učitavanju svih korisnika: {}", e.getMessage());
        }

        return users;
    }

    @Override
    public void update(User user) {
        try(Connection conn = DatabaseUtil.createConnection();
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER_QUERY)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getUserType().getType());
            statement.setString(4, user.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void delete(String id) {
        try(Connection conn = DatabaseUtil.createConnection();
            PreparedStatement statement = conn.prepareStatement(DELETE_USER_QUERY)) {

            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String userId = rs.getString(COLUMN_ID);
        String username = rs.getString(COLUMN_USERNAME);
        String password = rs.getString(COLUMN_PASSWORD);
        UserType userType = UserType.fromString(rs.getString(COLUMN_USER_TYPE));
        logger.info("hash password je: {}", password);

        return switch(userType) {
            case ADMIN -> new AdminUser(userId, username, password, userType);
            case USER -> new RegularUser(userId, username, password, userType);
        };
    }
}
