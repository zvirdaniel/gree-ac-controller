package com.gree.airconditioner.dto.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Temperature {
    @JsonProperty("SetTem")
    private final Integer temperature;

    @JsonProperty("TemUn")
    private final TemperatureUnit unit;
}
