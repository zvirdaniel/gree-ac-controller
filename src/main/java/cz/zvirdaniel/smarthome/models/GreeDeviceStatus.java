package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreeDeviceStatus {
	@JsonProperty("Pow")
	private Boolean power;

	@JsonProperty("Mod")
	private OperationMode operationMode;

	@JsonProperty("SetTem")
	private Integer temperature;

	@JsonProperty("TemUn")
	private TemperatureUnit temperatureUnit;

	@JsonProperty("WdSpd")
	private FanSpeed fanSpeed;

	@JsonProperty("Air")
	private Boolean air;

	@JsonProperty("Blo")
	private Boolean xFan;

	@JsonProperty("Health")
	private Boolean coldPlasmaGenerator;

	@JsonProperty("SwhSlp")
	private Boolean sleepMode;

	@JsonProperty("Lig")
	private Boolean lightIndicator;

	@JsonProperty("SwUpDn")
	private VerticalSwingDirection verticalSwingDirection;

	@JsonProperty("SwingLfRig")
	private HorizontalSwingDirection horizontalSwingDirection;

	@JsonProperty("Quiet")
	private Boolean quiet;

	@JsonProperty("Tur")
	private Boolean turbo;

	@JsonProperty("SvSt")
	private Boolean energySavingMode;

	@JsonProperty("StHt")
	private Boolean winterMaintenanceMode;
}
