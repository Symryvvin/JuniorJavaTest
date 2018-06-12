package ru.aizen.util;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import ru.aizen.model.TestData;

import java.util.ArrayList;
import java.util.List;

public class CustomXmlHandler extends DefaultHandler {
    private List<TestData> data;

    public CustomXmlHandler() {
        this.data = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("entry")) {
            data.add(new TestData(Integer.parseInt(attributes.getValue(0))));
        }
    }

    public List<TestData> getData() {
        return data;
    }
}