package com.gree.airconditioner.dto.packs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gree.airconditioner.models.GreeDeviceBinding;
import com.gree.airconditioner.utils.CryptoUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.gree.airconditioner.Application.OBJECT_MAPPER;

@Slf4j
public class StatusRequestPack {
    @JsonProperty("mac")
    private String mac;

    @JsonProperty("t")
    private String t;

    @JsonProperty("cols")
    private String[] cols;

    public StatusRequestPack(String mac) {
        this.mac = mac;
        this.t = "status";
        this.cols = new String[]{
                "Pow",
                "Mod",
                "SetTem",
                "WdSpd",
                "Air",
                "Blo",
                "Health",
                "SwhSlp",
                "Lig",
                "SwingLfRig",
                "SwUpDn",
                "Quiet",
                "Tur",
                "StHt",
                "TemUn",
                "HeatCoolType",
                "TemRec",
                "SvSt"
        };
    }

    @SneakyThrows
    public String encrypted(final String aesKey) {
        return CryptoUtil.encryptPack(aesKey.getBytes(), OBJECT_MAPPER.writeValueAsString(this));
    }
}
