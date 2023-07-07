package cz.zvirdaniel.smarthome.models.gree;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import lombok.Data;

@Data
public class GreeStatusContent {
    public static String[] COLUMNS = new String[]{
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
            "SvSt",
            "TemSen"
    };

    @JsonProperty("mac")
    private String mac;

    @JsonProperty("t")
    private GreeType type;

    @JsonProperty("r")
    private String responseCode;

    @JsonProperty("cols")
    private String[] columns;

    @JsonProperty("dat")
    private String[] data;
}
