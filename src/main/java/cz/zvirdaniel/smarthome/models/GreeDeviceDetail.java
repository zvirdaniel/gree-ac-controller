package cz.zvirdaniel.smarthome.models;

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

	@Schema(description = "temperature in temperature unit")
	private Integer temperature;

	@Schema(description = "unit for temperature")
	private TemperatureUnit temperatureUnit;

	@Schema(description = "speed of the integrated fan")
	private FanSpeed fanSpeed;

	@Schema(description = "controls the fresh air valve")
	private Boolean air;

	@Schema(description = "controls X-Fan function in Dry or Cool mode")
	private Boolean xFan;

	@Schema(description = "controls cold plasma ion generator")
	private Boolean coldPlasmaGenerator;

	@Schema(description = "controls sleep mode which gradually lowers the effectivity")
	private Boolean sleepMode;

	@Schema(description = "controls light indicators of the unit")
	private Boolean lightIndicator;

	@Schema(description = "controls the swing mode of the vertical air blades")
	private VerticalSwingDirection verticalSwingDirection;

	@Schema(description = "controls the swing mode of the horizontal air blades")
	private HorizontalSwingDirection horizontalSwingDirection;

	@Schema(description = "controls the quiet mode which slows down the fan to its most quiet speed in Cool or Heat mode")
	private Boolean quiet;

	@Schema(description = "controls the turbo fan mode in Dry or Cool mode")
	private Boolean turbo;

	@Schema(description = "controls energy saving mode")
	private Boolean energySavingMode;

	@Schema(description = "controls 8C winter maintenance mode")
	private Boolean winterMaintenanceMode;

	public GreeDeviceDetail(GreeDevice device, GreeDeviceStatus status) {
		this.name = device.getName();
		this.version = device.getVersion();
		this.macAddress = device.getMacAddress();
		this.address = device.getAddress().getHostAddress();
		this.port = device.getPort();
		this.power = status.getPower();
		this.operationMode = status.getOperationMode();
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
