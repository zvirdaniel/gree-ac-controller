package cz.zvirdaniel.smarthome.models.requests;

import cz.zvirdaniel.smarthome.models.GreeData;
import cz.zvirdaniel.smarthome.models.GreeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request used to scan all Gree devices on the network
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GreeScanRequest extends GreeData implements GreeRequest {
    public GreeScanRequest() {
        super.setType(GreeType.SCAN);
    }
}
