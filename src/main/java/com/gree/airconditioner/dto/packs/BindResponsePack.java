package com.gree.airconditioner.dto.packs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BindResponsePack {
    @JsonProperty("t")
    private String t;

    @JsonProperty("mac")
    private String mac;

    @JsonProperty("key")
    private String key;

    @JsonProperty("r")
    private String r;

    @Override
    public String toString() {
        return "BindResponsePack{" +
                "t='" + t + '\'' +
                ", mac='" + mac + '\'' +
                ", key='" + key + '\'' +
                ", r='" + r + '\'' +
                '}';
    }
}
