package cz.zvirdaniel.smarthome.models.gree.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerticalSwingDirection implements StatusEnum {
    DEFAULT(0, "Default"),

    FIXED_UPMOST_POSITION(2, "Fixed in upmost position"),
    FIXED_MIDDLE_UP_POSITION(3, "Fixed in middle-up position"),
    FIXED_MIDDLE_POSITION(4, "Fixed in middle position"),
    FIXED_MIDDLE_LOW_POSITION(5, "Fixed in upmost position"),
    FIXED_LOWEST_POSITION(6, "Fixed in lowest position"),

    SWING_FULL_RANGE(1, "Swing in full range"),
    SWING_UPMOST_POSITION(11, "Swing in upmost position"),
    SWING_MIDDLE_UP_POSITION(10, "Swing in middle-up position"),
    SWING_MIDDLE_POSITION(9, "Swing in middle position"),
    SWING_MIDDLE_LOW_POSITION(8, "Swing in low position"),
    SWING_LOWEST_POSITION(7,  "Swing in lowest position");

    private final int status;
    private final String description;

    @JsonCreator
    public static VerticalSwingDirection forStatus(Integer status) {
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
