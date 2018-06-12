package ru.aizen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Logger logger = LogManager.getLogger(AppConfig.class);

    private static final String SOURCE = "xml.source.path";
    private static final String TARGET = "xml.target.path";
    private static final String TRANSFORM = "xsl.transform.path";
    private static final String DB_URL = "datasource.jdbcUrl";
    private static final String DB_USER = "datasource.username";
    private static final String DB_PASSWORD = "datasource.password";

    private static final String CONFIG_PATH = "/app.properties";

    private static Properties appProperties;
    private static Properties dbProperties;

    public static void configureApp() {
        appProperties = loadProperties(CONFIG_PATH);
    }

    public static void configureDb(String path) {
        dbProperties = loadProperties(path);
    }

    private static Properties loadProperties(String path) {
        logger.info("Инициализация " + path);
        Properties properties = new Properties();
        try (InputStream in = AppConfig.class.getResourceAsStream(path)) {
            if (in != null) {
                properties.load(in);
            } else {
                throw new FileNotFoundException("Файл свойств не найден " + path);
            }
        } catch (IOException e) {
            logger.error("Приложение завершилось с ошибкой", e);
            System.exit(0);
        }
        return properties;
    }

    public static String getSource() {
        return appProperties.getProperty(SOURCE);
    }

    public static String getTarget() {
        return appProperties.getProperty(TARGET);
    }

    public static String getTransform() {
        return appProperties.getProperty(TRANSFORM);
    }

    public static String getDbUsername() {
        return dbProperties.getProperty(DB_USER);
    }

    public static String getDbPassword() {
        return dbProperties.getProperty(DB_PASSWORD);
    }

    public static String getDbUrl() {
        return dbProperties.getProperty(DB_URL);
    }
}
