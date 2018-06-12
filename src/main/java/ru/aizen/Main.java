package ru.aizen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aizen.model.DataProcessor;
import ru.aizen.util.DataSourceFactory;
import ru.aizen.util.Validator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static int count;

    public static void main(String[] args) {
        initialize();
        readEntryCount();

        logger.info("------------------------------ Старт ------------------------------");
        logger.info("Число записей = " + count);
        long time = System.currentTimeMillis();
        DataSource dataSource = DataSourceFactory.getSQLiteDataSource();

        try (Connection connection = dataSource.getConnection()) {
            DataProcessor processor = new DataProcessor(connection);
            long sum = processor.process(count);
            logger.info("Сумма: " + sum);
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("Общее время выполнения программы: " + (System.currentTimeMillis() - time) / 1000d + " с.");
    }

    private static void initialize() {
        AppConfig.configureApp();
        AppConfig.configureDb("/sqlite_db.properties");
    }

    private static void readEntryCount() {
        System.out.println("Введите число от 0 до 10 000 000");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            count = scanner.nextInt();
            if (Validator.validateCount(count))
                break;
        }
    }
}
