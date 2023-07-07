package cz.zvirdaniel.smarthome.models.gree;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import lombok.Data;

@Data
public class GreeCommandContent {
    @JsonProperty("t")
    private GreeType type;

    @JsonProperty("opt")
    private String[] columns;

    @JsonProperty("p")
    private Object[] data;
}
