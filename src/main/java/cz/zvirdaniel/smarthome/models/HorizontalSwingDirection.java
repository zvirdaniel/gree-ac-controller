package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HorizontalSwingDirection implements StatusEnum {
    DEFAULT(0),
    SWING_FULL_RANGE(1),
    SWING_LEFTMOST_POSITION(2),
    SWING_MIDDLE_LEFT_POSITION(3),
    SWING_MIDDLE_POSITION(4),
    SWING_MIDDLE_RIGHT_POSITION(5),
    SWING_RIGHTMOST_POSITION(6);

    @JsonValue
    private final int status;
}
