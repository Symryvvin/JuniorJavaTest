package ru.aizen.model;

public class TestData {
    private int fieldValue;

    public TestData(int fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getFieldValue() {
        return fieldValue;
    }

    @Override
    public String toString() {
        return "TestData{" +
                "fieldValue=" + fieldValue +
                '}';
    }
}
