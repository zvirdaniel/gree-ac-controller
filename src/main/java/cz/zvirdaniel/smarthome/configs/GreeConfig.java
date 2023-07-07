package cz.zvirdaniel.smarthome.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "gree")
public class GreeConfig {
    private Map<String, GreeDeviceConfig> devices = new HashMap<>();

    public record GreeDeviceConfig(String name) {}
}
