package ru.aizen.util;

public final class Validator {

    private Validator() {
        throw new AssertionError();
    }

    public static boolean validateCount(int value) {
        if (value < 0) {
            System.out.println("Количество записей меньше 0 \"count = " + value + "\"");
            return false;
        }
        if (value > 10000000) {
            System.out.println("Количество записей превышает ограничение в 10000000 \"count = " + value + "\"");
            return false;
        }
        return true;
    }
}
