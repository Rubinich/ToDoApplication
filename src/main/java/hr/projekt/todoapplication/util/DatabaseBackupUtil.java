package hr.projekt.todoapplication.util;

import hr.projekt.todoapplication.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseBackupUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupUtil.class);
    private static final String BACKUP_SUFFIX = "_BACKUP";

    public static void createBackup(String tableName) {
        if (!tableName.matches("^[A-Z_]+$")) {
            logger.error("Nevažeći naziv tablice: {}", tableName);
            throw new IllegalArgumentException("Nevažeći naziv tablice: " + tableName);
        }

        Thread.ofVirtual().start(() -> {
            String backupTableName = tableName + BACKUP_SUFFIX;

            try(Connection conn = DatabaseUtil.createConnection();
                Statement statement = conn.createStatement()) {

                statement.addBatch("DROP TABLE IF EXISTS " + backupTableName);
                statement.addBatch("CREATE TABLE " + backupTableName + " AS SELECT * FROM " + tableName);
                statement.executeBatch();

            } catch (SQLException e) {
                logger.error("Greska prilikom kreiranja backupa za tablicu {}", tableName);
                throw new DatabaseException(e);
            }
        });
    }
}
