package com.learnbridge.learn_bridge_back_end.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.YearMonth;

public class CustomYearMonthSerializer extends StdSerializer<YearMonth> {

    public CustomYearMonthSerializer() {
        super(YearMonth.class);
    }

    @Override
    public void serialize(YearMonth value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        String formattedDate = String.format("%02d/%02d", value.getMonthValue(), value.getYear() % 100);
        gen.writeString(formattedDate);
    }
}