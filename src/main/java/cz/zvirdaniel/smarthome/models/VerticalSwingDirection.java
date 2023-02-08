package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerticalSwingDirection implements StatusEnum {
    DEFAULT(0),

    FIXED_UPMOST_POSITION(2),
    FIXED_MIDDLE_UP_POSITION(3),
    FIXED_MIDDLE_POSITION(4),
    FIXED_MIDDLE_LOW_POSITION(5),
    FIXED_LOWEST_POSITION(6),

    SWING_FULL_RANGE(1),
    SWING_UPMOST_POSITION(11),
    SWING_MIDDLE_UP_POSITION(10),
    SWING_MIDDLE_POSITION(9),
    SWING_MIDDLE_LOW_POSITION(8),
    SWING_LOWEST_POSITION(7);

    @JsonValue
    private final int status;
}
