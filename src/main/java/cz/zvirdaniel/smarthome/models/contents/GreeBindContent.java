package cz.zvirdaniel.smarthome.models.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zvirdaniel.smarthome.models.GreeType;
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
