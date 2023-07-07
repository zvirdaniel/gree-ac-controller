package cz.zvirdaniel.smarthome.models.gree.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HorizontalSwingDirection implements StatusEnum {
    DEFAULT(0, "Default"),
    SWING_FULL_RANGE(1, "Swing in full range"),
    SWING_LEFTMOST_POSITION(2, "Swing in leftmost position"),
    SWING_MIDDLE_LEFT_POSITION(3, "Swing in left position"),
    SWING_MIDDLE_POSITION(4, "Swing in middle position"),
    SWING_MIDDLE_RIGHT_POSITION(5, "Swing in right position"),
    SWING_RIGHTMOST_POSITION(6, "Swing in rightmost position");

    private final int status;
    private final String description;

    @JsonCreator
    public static HorizontalSwingDirection forStatus(Integer status) {
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
