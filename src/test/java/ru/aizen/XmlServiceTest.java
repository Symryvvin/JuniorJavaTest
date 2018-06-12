package ru.aizen;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.aizen.service.XmlService;
import ru.aizen.util.DataSourceFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

public class XmlServiceTest {
    private XmlService service;
    private Connection connection;

    @Before
    public void init() throws SQLException {
        AppConfig.configureDb("/sqlite_db.properties");
        connection = DataSourceFactory.getSQLiteDataSource().getConnection();
    }

    @After
    public void close() throws SQLException {
        if (connection != null)
            connection.close();
    }

    private void initService() {
        service = new XmlService(connection);
        service.fillTable(10);
    }

    @Test
    public void testXMLWrite() throws IOException {
        initService();
        service.writeTableDataAsXML("temp.xml");
        String expectedXml = Files.readAllLines(Paths.get("temp.xml")).get(0);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<entries>" +
                "<entry><field>1</field></entry><entry><field>2</field></entry>" +
                "<entry><field>3</field></entry><entry><field>4</field></entry>" +
                "<entry><field>5</field></entry><entry><field>6</field></entry>" +
                "<entry><field>7</field></entry><entry><field>8</field></entry>" +
                "<entry><field>9</field></entry><entry><field>10</field></entry>" +
                "</entries>", expectedXml);
        Files.deleteIfExists(Paths.get("temp.xml"));
    }

    @Test
    public void testTransformation() throws IOException {
        initService();
        service.transform("/transform.xsl", "test/1.xml", "temp.xml");
        String expectedXml = Files.readAllLines(Paths.get("temp.xml")).get(0);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<entries>" +
                "<entry field=\"1\"/><entry field=\"2\"/><entry field=\"3\"/>" +
                "<entry field=\"4\"/><entry field=\"5\"/><entry field=\"6\"/>" +
                "<entry field=\"7\"/><entry field=\"8\"/><entry field=\"9\"/>" +
                "<entry field=\"10\"/>" +
                "</entries>", expectedXml);
        Files.deleteIfExists(Paths.get("temp.xml"));
    }


    @Test
    public void testSumCalculate() {
        initService();
        long expected = service.getFieldSumFromXml("test/2.xml");
        Assert.assertEquals(55, expected);
    }
}
