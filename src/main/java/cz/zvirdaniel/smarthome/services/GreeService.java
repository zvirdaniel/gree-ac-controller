package cz.zvirdaniel.smarthome.services;

import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.models.gree.GreeBinding;
import cz.zvirdaniel.smarthome.models.gree.GreeCommandRequest;
import cz.zvirdaniel.smarthome.models.gree.GreeData;
import cz.zvirdaniel.smarthome.models.gree.GreeDevice;
import cz.zvirdaniel.smarthome.models.gree.GreeDeviceDetail;
import cz.zvirdaniel.smarthome.models.gree.GreeDeviceStatus;
import cz.zvirdaniel.smarthome.models.gree.GreeStatusContent;
import cz.zvirdaniel.smarthome.models.gree.GreeStatusRequest;
import cz.zvirdaniel.smarthome.models.gree.enums.FanSpeed;
import cz.zvirdaniel.smarthome.models.gree.enums.HorizontalSwingDirection;
import cz.zvirdaniel.smarthome.models.gree.enums.OperationMode;
import cz.zvirdaniel.smarthome.models.gree.enums.TemperatureUnit;
import cz.zvirdaniel.smarthome.models.gree.enums.VerticalSwingDirection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeService {
    private final GreeCommunicationService communicationService;

    public Collection<GreeDevice> scanNetworkDevices() {
        return communicationService.scanAllNetworkDevices();
    }

    public boolean isConnected() {
        return communicationService.isConnected();
    }

    public Collection<GreeDevice> getConnectedDevices() {
        return communicationService.getConnectedDevices();
    }

    public Collection<GreeDevice> getTargetDevices(@Nullable String mac) {
        if (StringUtils.hasText(mac)) {
            final Set<GreeDevice> devices = this.getConnectedDevices().stream()
                                                .filter(it -> mac.equalsIgnoreCase(it.macAddress()))
                                                .collect(Collectors.toUnmodifiableSet());
            if (devices.size() != 1) {
                throw new RuntimeException("Device with MAC " + mac + " not found!");
            }

            return devices;
        } else {
            return this.getConnectedDevices();
        }
    }

    public List<GreeDeviceDetail> changeOperationMode(OperationMode mode) {
        log.info("Changing operation mode on all devices to {}", mode);
        final Collection<GreeDevice> devices = this.getTargetDevices(null);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .operationMode(mode)
                                          .xFan(mode == OperationMode.COOL || mode == OperationMode.DRY)
                                          .build(), devices);
        return this.getStatus(devices);
    }

    public List<GreeDeviceDetail> changePowerStatus(@Nullable String mac, boolean online) {
        final Collection<GreeDevice> devices = this.getTargetDevices(mac);
        log.info("Turning {} {}", online ? "on" : "off", devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .power(online)
                                          .build(), devices);
        return this.getStatus(devices);
    }

    public List<GreeDeviceDetail> changeTemperature(@Nullable String mac, Integer temperature) {
        final Collection<GreeDevice> devices = this.getTargetDevices(mac);
        log.info("Setting temperature to {}C on {}", temperature, devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .temperature(temperature)
                                          .temperatureUnit(TemperatureUnit.CELSIUS)
                                          .build(), devices);
        return this.getStatus(devices);
    }

    public List<GreeDeviceDetail> changeFanSpeed(@Nullable String mac, FanSpeed fanSpeed) {
        final Collection<GreeDevice> devices = this.getTargetDevices(mac);
        log.info("Setting fan speed to {} on {}", fanSpeed, devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .fanSpeed(fanSpeed)
                                          .build(), devices);
        return this.getStatus(devices);
    }

    public List<GreeDeviceDetail> changeSwing(@Nullable String mac, HorizontalSwingDirection horizontalSwingDirection, VerticalSwingDirection verticalSwingDirection) {
        final Collection<GreeDevice> devices = this.getTargetDevices(mac);
        log.info("Setting swing to horizontal {} and vertical {} on {}", horizontalSwingDirection, verticalSwingDirection, devices);
        this.changeStatus(GreeDeviceStatus.builder()
                                          .horizontalSwingDirection(horizontalSwingDirection)
                                          .verticalSwingDirection(verticalSwingDirection)
                                          .build(), devices);
        return this.getStatus(devices);
    }

    public void changeStatus(GreeDeviceStatus status, Collection<GreeDevice> devices) {
        Objects.requireNonNull(devices);
        for (final var device : devices) {
            final GreeBinding binding = communicationService.getBinding(device);
            final GreeCommandRequest request = new GreeCommandRequest(status, binding);
            communicationService.sendRequest(device, request);
        }
    }

    public List<GreeDeviceDetail> getStatus(Collection<GreeDevice> devices) {
        return devices.stream()
                      .map(this::getStatus)
                      .collect(Collectors.toList());
    }

    @SneakyThrows
    public GreeDeviceDetail getStatus(GreeDevice device) {
        log.info("Fetching status of device {}", device);
        final GreeBinding binding = communicationService.getBinding(device);
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
}
