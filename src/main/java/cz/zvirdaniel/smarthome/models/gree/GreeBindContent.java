package cz.zvirdaniel.smarthome.models.gree;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import lombok.Data;

@Data
public class GreeBindContent {
    @JsonProperty("t")
    private GreeType type;

    @JsonProperty("mac")
    private String mac;

    @JsonProperty("key")
    private String key;

    @JsonProperty("r")
    private String responseCode;

    @JsonProperty("uid")
    private Integer uid;
}
