package ru.aizen.dao;

import ru.aizen.model.TestData;

import java.sql.SQLException;
import java.util.List;

public interface TestDAO {

    void createTable();

    void truncate();

    void insertValues(int count);

    List<TestData> getTestData();

}
