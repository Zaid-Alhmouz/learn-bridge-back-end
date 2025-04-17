package com.learnbridge.learn_bridge_back_end.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, String> {

    // convert YearMonth to String in MM/YY format for the database
    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        if(attribute == null) {
            return null;
        }

        // format as MM/YY
        return String.format("%02d/%02d", attribute.getMonthValue(), attribute.getYear() % 100);
    }

    // convert String from the database to YearMonth
    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        if(dbData == null || dbData.isEmpty()) {
            return null;
        }

        String[] parts = dbData.split("/");
        if(parts.length != 2) {
            throw new IllegalArgumentException("Invalid yearMonth attribute: " + dbData);
        }

        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        // adjust year (assuming years less than 50 are 2000's)
        int fullYear = (year < 50 ? 2000 + year : 1900 + year);

        return YearMonth.of(fullYear, month);
    }
}