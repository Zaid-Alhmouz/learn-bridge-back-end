package com.learnbridge.learn_bridge_back_end.converter;

import com.learnbridge.learn_bridge_back_end.entity.SessionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class CaseInsensitiveEnumConverter
        implements AttributeConverter<SessionStatus,String> {
    @Override
    public String convertToDatabaseColumn(SessionStatus status) {
        return status == null ? null : status.name();
    }
    @Override
    public SessionStatus convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }

        return Arrays.stream(SessionStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown SessionStatus value: " + dbValue)
                );
    }
}

