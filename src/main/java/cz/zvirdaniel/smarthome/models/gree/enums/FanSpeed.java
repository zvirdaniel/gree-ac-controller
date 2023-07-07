package cz.zvirdaniel.smarthome.models.gree.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FanSpeed implements StatusEnum {
    AUTO(0, "Automatic"),
    LOW(1, "Low"),
    MEDIUM_LOW(2, "Medium low"),
    MEDIUM(3, "Medium"),
    MEDIUM_HIGH(4, "Medium high"),
    HIGH(5, "High");

    private final int status;
    private final String description;

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
