package cz.zvirdaniel.smarthome.models.gree;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GreeStatusRequest extends GreeData implements GreeRequest {
    @SneakyThrows
    public GreeStatusRequest(GreeBinding deviceBinding) {
        super.setType(GreeType.PACK);
        super.setCid("app");
        super.setI(0);
        super.setTcid(deviceBinding.getDevice().macAddress());
        super.setUid(0L);

        final GreeStatusContent content = new GreeStatusContent();
        content.setMac(deviceBinding.getDevice().macAddress());
        content.setType(GreeType.STATUS);
        content.setColumns(GreeStatusContent.COLUMNS);
        super.setEncryptedContent(deviceBinding.encryptContent(Application.OBJECT_MAPPER.writeValueAsString(content)));
    }
}
