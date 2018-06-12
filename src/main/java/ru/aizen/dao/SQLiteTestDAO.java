package ru.aizen.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Language;
import ru.aizen.model.TestData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteTestDAO implements TestDAO {
    private static final Logger logger = LogManager.getLogger(SQLiteTestDAO.class);
    private static final int BATCH_SIZE = 10000;

    private Connection connection;

    public SQLiteTestDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createTable() {
        @Language("SQLite")
        String createQuery = "CREATE TABLE IF NOT EXISTS tbl_test (field INT);\n" +
                " CREATE INDEX IF NOT EXISTS tbl_test_field_index ON tbl_test (field)";
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(createQuery);
        } catch (SQLException e) {
            logger.error("Ошибка во время выполнения запроса " + createQuery, e);
        }
    }

    @Override
    public void truncate() {
        logger.info("Очищаем таблицу TBL_TEST");
        @Language("SQLite")
        String deleteQuery = "DELETE FROM tbl_test;\n" +
                "VACUUM";
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            logger.error("Ошибка во время выполнения запроса " + deleteQuery, e);
        }
    }

    @Override
    public void insertValues(int count) {
        long time = System.currentTimeMillis();
        logger.info("Вставляем данные в таблицу TBL_TEST...");
        @Language("SQLite")
        String batchQuery = "INSERT INTO tbl_test (field) VALUES (?)";
        try (PreparedStatement insert = connection.prepareStatement(batchQuery)) {
            connection.setAutoCommit(false);
            for (int i = 1; i <= count; i++) {
                insert.setInt(1, i);
                insert.addBatch();
                if (i % BATCH_SIZE == 0 || i == count) {
                    insert.executeBatch();
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
            logger.info("Таблица заполнена за " + (System.currentTimeMillis() - time) / 1000d + " с.");
        } catch (SQLException e) {
            logger.error("Ошибка во время выполнения запроса " + batchQuery, e);
        }
    }

    @Override
    public List<TestData> getTestData() {
        long time = System.currentTimeMillis();
        logger.info("Получаем данные из таблицы TBL_TEST...");
        List<TestData> result = new ArrayList<>();
        @Language("SQLite")
        String query = "SELECT field FROM tbl_test";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TestData(rs.getInt("field")));
            }
        } catch (SQLException e) {
            logger.error("Ошибка во время выполнения запроса " + query, e);
        }
        logger.info("Данные получены за " + (System.currentTimeMillis() - time) / 1000d + " с.");
        return result;
    }
}
