package com.gree.airconditioner.dto.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationMode {
    AUTO(0),
    COOL(1),
    DRY(2),
    FAN(3),
    HEAT(4);

    private final int status;

    public static OperationMode fromCode(int rawStatus) {
        for (OperationMode value : values()) {
            if (value.getStatus() == rawStatus) {
                return value;
            }
        }
        return null;
    }
}
