package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonValue
    private final int status;
}
