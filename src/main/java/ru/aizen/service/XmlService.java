package ru.aizen.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import ru.aizen.dao.SQLiteTestDAO;
import ru.aizen.dao.TestDAO;
import ru.aizen.model.TestData;
import ru.aizen.util.CustomXmlHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;

public class XmlService {
    private static final Logger logger = LogManager.getLogger(XmlService.class);
    private TestDAO testDAO;

    public XmlService(Connection connection) {
        testDAO = new SQLiteTestDAO(connection);
    }

    public void fillTable(int count) {
        testDAO.createTable();
        testDAO.truncate();
        testDAO.insertValues(count);
    }

    public void transform(String xslSource, String in, String out) {
        long time = System.currentTimeMillis();
        logger.info("Трансформация " + in + " в " + out + " с помощью XSL " + xslSource);
        TransformerFactory factory = TransformerFactory.newInstance();
        try (InputStream transformSource = getClass().getResourceAsStream(xslSource);
             InputStream source = Files.newInputStream(Paths.get(in));
             OutputStream result = Files.newOutputStream(Paths.get(out))) {
            Transformer transformer = factory.newTransformer(new StreamSource(transformSource));
            StreamSource streamSource = new StreamSource(source);
            StreamResult streamResult = new StreamResult(result);
            transformer.transform(streamSource, streamResult);
            logger.info("Трансформация прошла успешно за " + (System.currentTimeMillis() - time) / 1000d + " с.");
        } catch (IOException | TransformerException e) {
            logger.error(e);
        }
    }

    public void writeTableDataAsXML(String path) {
        long time = System.currentTimeMillis();
        logger.info("Пишем значение полей field таблицы TBL_TEST в файл " + path);
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(path)))) {
            List<TestData> data = testDAO.getTestData();
            // ВАЖНО. Кодировка createXMLStreamWriter должна быть равна кодировки в методе writeStartDocument(),
            // иначе в jar получим javax.xml.stream.XMLStreamException: Underlying stream encoding 'Cp1251'
            // and input parameter for writeStartDocument() method 'UTF-8' do not match.
            XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(out,"UTF-8");
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("entries");
            for (TestData test : data) {
                writer.writeStartElement("entry");
                writer.writeStartElement("field");
                writer.writeCharacters(String.valueOf(test.getFieldValue()));
                writer.writeEndElement();
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
            logger.info("Запись завершена за " + (System.currentTimeMillis() - time) / 1000d + " с.");
        } catch (IOException | XMLStreamException e) {
            logger.error(e);
        }
    }

    public Long getFieldSumFromXml(String source) {
        logger.info("Подсчитываем сумму всех значений атрибутов field в файле " + source);
        try (InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(source)))) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            CustomXmlHandler customHandler = new CustomXmlHandler();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(in, customHandler);
            return customHandler.getData()
                    .stream()
                    .mapToLong(TestData::getFieldValue)
                    .sum();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            logger.error(e);
        }
        return null;
    }
}
