package com.gree.airconditioner.dto.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Switch {
    OFF(0),
    ON(1);

    private final int status;

    public static Switch fromCode(int rawStatus) {
        for (Switch value : values()) {
            if (value.getStatus() == rawStatus) {
                return value;
            }
        }
        return null;
    }
}
