package ru.aizen;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.aizen.dao.SQLiteTestDAO;
import ru.aizen.dao.TestDAO;
import ru.aizen.model.TestData;
import ru.aizen.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SqliteDaoTest {
    private TestDAO dao;
    private Connection connection;

    @Before
    public void init() throws SQLException {
        AppConfig.configureDb("/sqlite_db.properties");
        connection = DataSourceFactory.getSQLiteDataSource().getConnection();
        dao = new SQLiteTestDAO(connection);
        dao.createTable();
        dao.truncate();
    }

    @After
    public void close() throws SQLException {
        if (connection != null)
            connection.close();
    }

    @Test
    public void testInsert() {
        dao.insertValues(5);
        List<TestData> data = dao.getTestData();
        Assert.assertEquals(5, data.size());
        Assert.assertEquals(1, data.get(0).getFieldValue());
    }

}
