package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationMode implements StatusEnum {
    AUTO(0),
    COOL(1),
    DRY(2),
    FAN(3),
    HEAT(4);

    private final int status;

    @JsonCreator
    public static OperationMode forStatus(Integer status) {
        if (status != null) {
            for (final var value : values()) {
                if (value.getStatus() == status) {
                    return value;
                }
            }
        }

        return null;
    }
}
