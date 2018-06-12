package ru.aizen.model;

import ru.aizen.AppConfig;
import ru.aizen.service.XmlService;

import java.sql.Connection;

public class DataProcessor {
    private String source;
    private String target;
    private String transform;

    private final Connection connection;

    public DataProcessor(Connection connection) {
        this.connection = connection;
        this.source = AppConfig.getSource();
        this.target = AppConfig.getTarget();
        this.transform = AppConfig.getTransform();
    }

    public long process(int count) {
        XmlService xmlService = new XmlService(connection);
        xmlService.fillTable(count);
        xmlService.writeTableDataAsXML(source);
        xmlService.transform(transform, source, target);
        return xmlService.getFieldSumFromXml(target);
    }
}
