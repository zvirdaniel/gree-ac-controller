package com.gree.airconditioner.dto.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GreeDeviceStatus {
    @JsonProperty("Pow")
    private Switch power;

    @JsonProperty("Mod")
    private OperationMode operationMode;

    @JsonUnwrapped
    private Temperature temperature;

    @JsonProperty("WdSpd")
    private FanMode fanMode;

    @JsonProperty("Air")
    private Switch air;

    @JsonProperty("Blo")
    private Switch blow;

    @JsonProperty("Health")
    private Switch health;

    @JsonProperty("SwhSlp")
    private Switch sleepMode;

    @JsonProperty("Lig")
    private Switch lightIndicator;

    @JsonProperty("SwUpDn")
    private SwingDirection swingDirection;

    @JsonProperty("Quiet")
    private Switch quiet;

    @JsonProperty("Tur")
    private Switch maximumIntensity;

    @JsonProperty("SvSt")
    private Switch energySavingMode;

    public GreeDeviceStatus(Switch power) {
        this.power = power;
    }
}
