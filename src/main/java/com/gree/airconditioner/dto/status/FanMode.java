package com.gree.airconditioner.dto.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FanMode {
    AUTO(0),
    LOW(1),
    MEDIUM_LOW(2),
    MEDIUM(3),
    MEDIUM_HIGH(4),
    HIGH(5);

    private final int status;

    public static FanMode fromCode(int rawStatus) {
        for (FanMode value : values()) {
            if (value.getStatus() == rawStatus) {
                return value;
            }
        }
        return null;
    }
}
