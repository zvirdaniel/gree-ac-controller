package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FanSpeed implements StatusEnum {
    AUTO(0),
    LOW(1),
    MEDIUM_LOW(2),
    MEDIUM(3),
    MEDIUM_HIGH(4),
    HIGH(5);

    private final int status;

    @JsonCreator
    public static FanSpeed forStatus(Integer status) {
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
