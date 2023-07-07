package cz.zvirdaniel.smarthome.models.gree;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import cz.zvirdaniel.smarthome.utils.CryptoUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GreeBindRequest extends GreeData implements GreeRequest {
    @SneakyThrows
    public GreeBindRequest(String macAddress) {
        super.setType(GreeType.PACK);
        super.setUid(1L);
        super.setCid("app");
        super.setI(1);
        super.setTcid(macAddress);

        final GreeBindContent content = new GreeBindContent();
        content.setMac(macAddress);
        content.setType(GreeType.BIND);
        content.setUid(0);
        final String message = Application.OBJECT_MAPPER.writeValueAsString(content);
        super.setEncryptedContent(CryptoUtil.encryptContent(CryptoUtil.AES_General_Key, message));
    }
}
