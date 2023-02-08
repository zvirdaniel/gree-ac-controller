package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureUnit implements StatusEnum {
    CELSIUS(0),
    FAHRENHEIT(1);

    @JsonValue
    private final int status;
}
