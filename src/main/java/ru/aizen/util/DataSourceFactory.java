package ru.aizen.util;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.aizen.AppConfig;

import javax.sql.DataSource;

public class DataSourceFactory {

    private DataSourceFactory() {
    }

    public static DataSource getSQLiteDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(AppConfig.getDbUrl());
        dataSource.setUsername(AppConfig.getDbUsername());
        dataSource.setPassword(AppConfig.getDbPassword());
        return dataSource;
    }
}
