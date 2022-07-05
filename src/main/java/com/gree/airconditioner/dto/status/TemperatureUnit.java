package com.gree.airconditioner.dto.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureUnit {
    CELSIUS(0),
    FAHRENHEIT(1);

    private final int status;

    public static TemperatureUnit fromCode(int rawStatus) {
        for (TemperatureUnit value : values()) {
            if (value.getStatus() == rawStatus) {
                return value;
            }
        }
        return null;
    }
}
