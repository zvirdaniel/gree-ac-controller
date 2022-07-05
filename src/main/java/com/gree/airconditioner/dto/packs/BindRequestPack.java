package com.gree.airconditioner.dto.packs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gree.airconditioner.utils.CryptoUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.gree.airconditioner.Application.OBJECT_MAPPER;

@Data
@Slf4j
@NoArgsConstructor
public class BindRequestPack {
    @JsonProperty("mac")
    private String mac;

    @JsonProperty("t")
    private String t;

    @JsonProperty("uid")
    private Integer uid;

    public BindRequestPack(String mac) {
        this.mac = mac;
        this.t = "bind";
        this.uid = 0;
    }

    @SneakyThrows
    public String encrypted() {
        return CryptoUtil.encryptPack(OBJECT_MAPPER.writeValueAsString(this));
    }
}
