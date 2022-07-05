package com.gree.airconditioner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommandResponse {
    @JsonProperty("t")
    private CommandType commandType;

    @JsonProperty("uid")
    private Long uid;

    @JsonProperty("cid")
    private String cid;

    @JsonProperty("i")
    private Integer i;

    @JsonProperty("pack")
    private String pack;

    @JsonProperty("tcid")
    private String tcid;

    @Override
    public String toString() {
        return "CommandResponse{" +
                "commandType=" + commandType +
                ", uid=" + uid +
                ", cid='" + cid + '\'' +
                ", i=" + i +
                ", pack='" + getPack() + '\'' +
                ", tcid='" + tcid + '\'' +
                '}';
    }

}
