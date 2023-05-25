package cz.zvirdaniel.smarthome.services;

import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.models.FanSpeed;
import cz.zvirdaniel.smarthome.models.GreeData;
import cz.zvirdaniel.smarthome.models.GreeDevice;
import cz.zvirdaniel.smarthome.models.GreeDeviceBinding;
import cz.zvirdaniel.smarthome.models.GreeDeviceDetail;
import cz.zvirdaniel.smarthome.models.GreeDeviceStatus;
import cz.zvirdaniel.smarthome.models.HorizontalSwingDirection;
import cz.zvirdaniel.smarthome.models.OperationMode;
import cz.zvirdaniel.smarthome.models.TemperatureUnit;
import cz.zvirdaniel.smarthome.models.VerticalSwingDirection;
import cz.zvirdaniel.smarthome.models.contents.GreeStatusContent;
import cz.zvirdaniel.smarthome.models.requests.GreeCommandRequest;
import cz.zvirdaniel.smarthome.models.requests.GreeStatusRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private final Set<GreeDevice> greeDevices = new HashSet<>();

    public GreeDevice[] getDeviceArray(@Nullable String mac) {
        if (greeDevices.isEmpty()) {
            throw new RuntimeException("No available devices!");
        }

        if (StringUtils.hasText(mac)) {
            final GreeDevice[] devices = greeDevices.stream()
                                                    .filter(it -> mac.equalsIgnoreCase(it.getMacAddress()))
                                                    .toArray(GreeDevice[]::new);
            if (ArrayUtils.getLength(devices) != 1) {
                throw new RuntimeException("Device with MAC " + mac + " not found!");
            }

            return devices;
        } else {
            return greeDevices.toArray(GreeDevice[]::new);
        }
    }

    public void changeOperationMode(OperationMode mode) {
        log.info("Changing operation mode on all devices to {}", mode);
        final GreeDevice[] devices = this.getDeviceArray(null);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .operationMode(mode)
                                          .xFan(mode == OperationMode.COOL || mode == OperationMode.DRY)
                                          .build(), devices);
    }

    public void changePowerStatus(@Nullable String mac, boolean online) {
        final GreeDevice[] devices = this.getDeviceArray(mac);
        log.info("Turning {} {}", online ? "on" : "off", devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .power(online)
                                          .build(), devices);
    }

    public void changeTemperature(@Nullable String mac, Integer temperature) {
        final GreeDevice[] devices = this.getDeviceArray(mac);
        log.info("Setting temperature to {}C on {}", temperature, devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .temperature(temperature)
                                          .temperatureUnit(TemperatureUnit.CELSIUS)
                                          .build(), devices);
    }

    public void changeFanSpeed(@Nullable String mac, FanSpeed fanSpeed) {
        final GreeDevice[] devices = this.getDeviceArray(mac);
        log.info("Setting fan speed to {} on {}", fanSpeed, devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .fanSpeed(fanSpeed)
                                          .build(), devices);
    }

    public void changeSwing(@Nullable String mac, HorizontalSwingDirection horizontalSwingDirection, VerticalSwingDirection verticalSwingDirection) {
        final GreeDevice[] devices = this.getDeviceArray(mac);
        log.info("Setting swing to horizontal {} and vertical {} on {}", horizontalSwingDirection, verticalSwingDirection, devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .horizontalSwingDirection(horizontalSwingDirection)
                                          .verticalSwingDirection(verticalSwingDirection)
                                          .build(), devices);
    }

    public void changeStatus(GreeDeviceStatus status, GreeDevice... devices) {
        Objects.requireNonNull(devices);
        for (final var device : devices) {
            final GreeDeviceBinding binding = communicationService.getBinding(device);
            final GreeCommandRequest request = new GreeCommandRequest(status, binding);
            communicationService.sendRequest(device, request);
        }
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
        this.greeDevices.addAll(communicationService.searchAllNetworkDevices());
    }
}
