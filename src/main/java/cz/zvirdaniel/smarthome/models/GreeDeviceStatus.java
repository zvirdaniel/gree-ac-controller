package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.zvirdaniel.smarthome.configs.StatusEnumSerializer;
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
	@JsonSerialize(using = StatusEnumSerializer.class)
	private OperationMode operationMode;

	@JsonProperty("SetTem")
	private Integer temperature;

	@JsonProperty("TemUn")
	@JsonSerialize(using = StatusEnumSerializer.class)
	private TemperatureUnit temperatureUnit;

	@JsonProperty("WdSpd")
	@JsonSerialize(using = StatusEnumSerializer.class)
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
	@JsonSerialize(using = StatusEnumSerializer.class)
	private VerticalSwingDirection verticalSwingDirection;

	@JsonProperty("SwingLfRig")
	@JsonSerialize(using = StatusEnumSerializer.class)
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
