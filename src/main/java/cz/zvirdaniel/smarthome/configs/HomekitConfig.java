package cz.zvirdaniel.smarthome.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "homekit")
public class HomekitConfig {
    private boolean active;
    private int port;
    private String pin;
    private String bridgeLabel;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String hardwareRevision;
}
