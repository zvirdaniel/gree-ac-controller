package cz.zvirdaniel.smarthome.models.gree;

import cz.zvirdaniel.smarthome.models.gree.enums.FanSpeed;
import cz.zvirdaniel.smarthome.models.gree.enums.HorizontalSwingDirection;
import cz.zvirdaniel.smarthome.models.gree.enums.OperationMode;
import cz.zvirdaniel.smarthome.models.gree.enums.TemperatureUnit;
import cz.zvirdaniel.smarthome.models.gree.enums.VerticalSwingDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GreeDeviceDetail {
    @Schema(description = "device name from the app config")
    private String name;

    @Schema(description = "device version")
    private String version;

    @Schema(description = "device MAC address")
    private String macAddress;

    @Schema(description = "device web address")
    private String address;

    @Schema(description = "device web port")
    private Integer port;

    @Schema(description = "power state of the device")
    private Boolean power;

    @Schema(description = "mode of operation")
    private OperationMode operationMode;

    @Schema(description = "current room temperature in temperature unit")
    private Integer temperatureSensor;

    @Schema(description = "target room temperature in temperature unit")
    private Integer temperature;

    @Schema(description = "unit for temperature")
    private TemperatureUnit temperatureUnit;

    @Schema(description = "speed of the integrated fan")
    private FanSpeed fanSpeed;

    @Schema(description = "fresh air valve")
    private Boolean air;

    @Schema(description = "eliminates remaining humidity after operation in Dry or Cool mode")
    private Boolean xFan;

    @Schema(description = "cold plasma ion generator")
    private Boolean coldPlasmaGenerator;

    @Schema(description = "sleep mode gradually lowers the effectivity")
    private Boolean sleepMode;

    @Schema(description = "light indicators of the unit")
    private Boolean lightIndicator;

    @Schema(description = "swing mode of the vertical air blades")
    private VerticalSwingDirection verticalSwingDirection;

    @Schema(description = "swing mode of the horizontal air blades")
    private HorizontalSwingDirection horizontalSwingDirection;

    @Schema(description = "quiet mode slows down the fan to its most quiet speed in Cool or Heat mode")
    private Boolean quiet;

    @Schema(description = "turbo fan mode in Dry or Cool mode")
    private Boolean turbo;

    @Schema(description = "energy saving mode")
    private Boolean energySavingMode;

    @Schema(description = "8C winter maintenance mode")
    private Boolean winterMaintenanceMode;

    public GreeDeviceDetail(GreeDevice device, GreeDeviceStatus status) {
        this.name = device.name();
        this.version = device.version();
        this.macAddress = device.macAddress();
        this.address = device.address().getHostAddress();
        this.port = device.port();
        this.power = status.getPower();
        this.operationMode = status.getOperationMode();
        this.temperatureSensor = status.getTemperatureSensor();
        this.temperature = status.getTemperature();
        this.temperatureUnit = status.getTemperatureUnit();
        this.fanSpeed = status.getFanSpeed();
        this.air = status.getAir();
        this.xFan = status.getXFan();
        this.coldPlasmaGenerator = status.getColdPlasmaGenerator();
        this.sleepMode = status.getSleepMode();
        this.lightIndicator = status.getLightIndicator();
        this.verticalSwingDirection = status.getVerticalSwingDirection();
        this.horizontalSwingDirection = status.getHorizontalSwingDirection();
        this.quiet = status.getQuiet();
        this.turbo = status.getTurbo();
        this.energySavingMode = status.getEnergySavingMode();
        this.winterMaintenanceMode = status.getWinterMaintenanceMode();
    }
}
