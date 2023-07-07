package cz.zvirdaniel.smarthome.models.gree;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import cz.zvirdaniel.smarthome.models.gree.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class GreeCommandRequest extends GreeData implements GreeRequest {
    @SneakyThrows
    public GreeCommandRequest(GreeDeviceStatus status, GreeBinding deviceBinding) {
        super.setType(GreeType.PACK);
        super.setCid("app");
        super.setI(0);
        super.setTcid(deviceBinding.getDevice().macAddress());
        super.setUid(0L);

        final Map<String, Object> statusMap =
                Application.OBJECT_MAPPER.convertValue(status, new TypeReference<HashMap<String, Object>>() {})
                                         .entrySet().stream()
                                         .filter(entry -> entry.getValue() != null)
                                         .map(entry -> Map.entry(entry.getKey(), this.mapGreeValue(entry.getValue())))
                                         .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        final GreeCommandContent content = new GreeCommandContent();
        content.setType(GreeType.COMMAND);
        content.setColumns(statusMap.keySet().toArray(String[]::new));
        content.setData(statusMap.values().toArray());

        final String contentRaw = Application.OBJECT_MAPPER.writeValueAsString(content);
        final String contentEncrypted = deviceBinding.encryptContent(contentRaw);
        super.setEncryptedContent(contentEncrypted);
    }

    private Object mapGreeValue(Object value) {
        if (value instanceof Integer || value instanceof Long || value instanceof String) {
            return value;
        } else if (value instanceof Boolean) {
            return Boolean.TRUE.equals(value) ? 1 : 0;
        } else if (value instanceof StatusEnum) {
            return ((StatusEnum) value).getStatus();
        }

        throw new IllegalArgumentException("Type " + value.getClass() + " is not supported");
    }
}
