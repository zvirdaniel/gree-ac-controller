package cz.zvirdaniel.smarthome.services;

import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.models.GreeData;
import cz.zvirdaniel.smarthome.models.GreeDevice;
import cz.zvirdaniel.smarthome.models.GreeDeviceBinding;
import cz.zvirdaniel.smarthome.models.GreeDeviceDetail;
import cz.zvirdaniel.smarthome.models.GreeDeviceStatus;
import cz.zvirdaniel.smarthome.models.HorizontalSwingDirection;
import cz.zvirdaniel.smarthome.models.TemperatureUnit;
import cz.zvirdaniel.smarthome.models.VerticalSwingDirection;
import cz.zvirdaniel.smarthome.models.contents.GreeStatusContent;
import cz.zvirdaniel.smarthome.models.requests.GreeCommandRequest;
import cz.zvirdaniel.smarthome.models.requests.GreeStatusRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeService {
	private final GreeCommunicationService communicationService;

	@Getter
	private final Set<GreeDevice> devices = new HashSet<>();

	public GreeDevice getDeviceByMac(String mac) {
		Objects.requireNonNull(mac);
		return this.devices.stream()
		                   .filter(it -> mac.equalsIgnoreCase(it.getMacAddress()))
		                   .findAny()
		                   .orElseThrow(() -> new RuntimeException("Device with MAC " + mac + " not found!"));
	}

	public void changePowerStatus(GreeDevice device, boolean online) {
		log.info("Turning {} {}", online ? "on" : "off", device);
		this.changeStatus(device, GreeDeviceStatus.builder()
		                                          .power(online)
		                                          .build());
	}

	public void changeTemperature(GreeDevice device, Integer temperature) {
		log.info("Setting temperature on {} to {}C", device, temperature);
		this.changeStatus(device, GreeDeviceStatus.builder()
		                                          .temperature(temperature)
		                                          .temperatureUnit(TemperatureUnit.CELSIUS)
		                                          .build());
	}

	public void changeSwing(GreeDevice device, HorizontalSwingDirection horizontalSwingDirection, VerticalSwingDirection verticalSwingDirection) {
		log.info("Setting swing on {} to horizontal {} and vertical {}", device, horizontalSwingDirection, verticalSwingDirection);
		this.changeStatus(device, GreeDeviceStatus.builder()
		                                          .horizontalSwingDirection(horizontalSwingDirection)
		                                          .verticalSwingDirection(verticalSwingDirection)
		                                          .build());
	}

	public void changeStatus(GreeDevice device, GreeDeviceStatus status) {
		final GreeDeviceBinding binding = communicationService.getBinding(device);
		final GreeCommandRequest request = new GreeCommandRequest(status, binding);
		communicationService.sendRequest(device, request);
	}

	@SneakyThrows
	public GreeDeviceDetail getStatus(GreeDevice device) {
		log.info("Fetching status of device {}", device);
		final GreeDeviceBinding binding = communicationService.getBinding(device);
		final String rawResponse = communicationService.sendRequest(device, new GreeStatusRequest(binding));
		final GreeData response = Application.OBJECT_MAPPER.readValue(rawResponse, GreeData.class);
		final String rawContent = binding.decryptContent(response.getEncryptedContent());
		final GreeStatusContent content = Application.OBJECT_MAPPER.readValue(rawContent, GreeStatusContent.class);

		if (content.getColumns().length != content.getData().length) {
			throw new IllegalStateException("Columns don't match data!");
		}
		final HashMap<String, String> data = new HashMap<>();
		for (int i = 0; i < content.getColumns().length; i++) {
			data.put(content.getColumns()[i], content.getData()[i]);
		}

		final GreeDeviceStatus status = Application.OBJECT_MAPPER.convertValue(data, GreeDeviceStatus.class);
		return new GreeDeviceDetail(device, status);
	}

	@Scheduled(fixedRate = 30 * 60, timeUnit = TimeUnit.SECONDS)
	private void addNetworkDevices() {
		this.devices.addAll(communicationService.searchAllNetworkDevices());
	}
}
