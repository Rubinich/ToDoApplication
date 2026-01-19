package hr.projekt.todoapplication.repository.database;

import hr.projekt.todoapplication.model.user.User;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public interface UserDao {
    void save(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsernameAndPassword(String username, String password) throws IOException;
    Set<User> findAll() throws IOException;
    void update(User user);
    void delete(String id);
}
