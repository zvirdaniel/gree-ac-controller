package cz.zvirdaniel.smarthome.models.gree;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import lombok.Data;
import lombok.Getter;

/**
 * This is a generic pack-type response which has a pack field that contains an embedded JSON object.
 * The pack is encrypted with AES128/ECB and encoded in Base64.
 * This response is encrypted using the "Generic AES key" which is the same for all devices.
 */
@Data
@Getter
public class GreeData {
    @JsonProperty("t")
    private GreeType type;

    @JsonProperty("uid")
    private Long uid;

    @JsonProperty("cid")
    private String cid;

    @JsonProperty("i")
    private Integer i;

    @JsonProperty("pack")
    private String encryptedContent;

    @JsonProperty("tcid")
    private String tcid;
}
