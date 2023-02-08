package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GreeType {
    SCAN("scan"),
    COMMAND("cmd"),
    DATA("dat"),
    STATUS("status"),
    BIND("bind"),
    BINDOK("bindok"),
    PACK("pack");

    @JsonValue
    private final String code;
}
