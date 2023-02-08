package cz.zvirdaniel.smarthome.models.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GreeScanContent {
    @JsonProperty("t")
    private String t;

    @JsonProperty("cid")
    private String cid;

    @JsonProperty("bc")
    private String brandCompany;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("catalog")
    private String catalog;

    @JsonProperty("mac")
    private String mac;

    @JsonProperty("mid")
    private String mid;

    @JsonProperty("model")
    private String model;

    @JsonProperty("name")
    private String friendlyName;

    @JsonProperty("series")
    private String series;

    @JsonProperty("vender")
    private String vender;

    @JsonProperty("ver")
    private String ver;

    @JsonProperty("lock")
    private Integer lock;
}