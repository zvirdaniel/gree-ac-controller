package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureUnit implements StatusEnum {
    CELSIUS(0),
    FAHRENHEIT(1);

    private final int status;

    @JsonCreator
    public static TemperatureUnit forStatus(Integer status) {
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
