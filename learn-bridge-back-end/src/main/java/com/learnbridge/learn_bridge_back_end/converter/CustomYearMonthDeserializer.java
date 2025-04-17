package com.learnbridge.learn_bridge_back_end.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.YearMonth;

public class CustomYearMonthDeserializer extends StdDeserializer<YearMonth> {

    public CustomYearMonthDeserializer() {
        super(YearMonth.class);
    }

    @Override
    public YearMonth deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] parts = value.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid expiry date format. Expected MM/YY but got: " + value);
        }

        try {
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            // Adjust year (assuming years less than 50 are 2000's)
            int fullYear = (year < 50 ? 2000 + year : 1900 + year);

            return YearMonth.of(fullYear, month);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid expiry date format. Expected MM/YY but got: " + value);
        }
    }
}