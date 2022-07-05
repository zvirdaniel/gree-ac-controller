package com.gree.airconditioner.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Command {
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

    public Command(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public String toString() {
        return "Command{" +
                "commandType=" + commandType +
                ", uid=" + uid +
                ", cid='" + cid + '\'' +
                ", i=" + i +
                ", pack='" + pack + '\'' +
                ", tcid='" + tcid + '\'' +
                '}';
    }
}
