package com.roomate.app.enumeration.converter;

import com.roomate.app.enumeration.Permissions;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Permissions, String> {
    @Override
    public String convertToDatabaseColumn(Permissions permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.getValue();
    }

    @Override
    public Permissions convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return Stream.of(Permissions.values())
                .filter(Permissions -> Permissions.getValue().equals(s))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
