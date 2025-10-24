package ru.practicum;

import java.time.format.DateTimeFormatter;

public class CustomDateTimeFormatter {

    // Создаем статическое поле с нужным форматом
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // Метод для получения форматировщика
    public static DateTimeFormatter getFormatter() {
        return DATE_TIME_FORMATTER;
    }
}