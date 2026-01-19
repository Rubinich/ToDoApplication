package hr.projekt.todoapplication.util;

import hr.projekt.todoapplication.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
    private static final String DATABASE_FILE = "src/main/resources/database.properties";

    private static final String USER_ID = "ID";
    private static final String USER_USERNAME = "USERNAME";

    public static Connection createConnection() throws DatabaseException {
        try(var reader = new FileReader(DATABASE_FILE)) {
            var properties = new Properties();
            properties.load(reader);

            var url = properties.getProperty("url");
            var username = properties.getProperty("username");
            var password = properties.getProperty("password");

            return DriverManager.getConnection(url, username, password);
        } catch (SQLException | IOException e) {
            throw new DatabaseException(e);
        }
    }

    public static void closeConnection(Connection conn) throws DatabaseException{
        try{
            conn.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static boolean testConnection() {
        try {
            logger.info("Testiram konekciju na H2 bazu...");
            Connection conn = createConnection();
            logger.info("Konekcija na bazu uspješna!");
            closeConnection(conn);
            return true;
        } catch (Exception e) {
            logger.error("Konekcija na bazu neuspješna: {}", e.getMessage());
            return false;
        }
    }
}
