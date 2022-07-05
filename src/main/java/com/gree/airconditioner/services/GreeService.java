package com.gree.airconditioner.services;

import com.gree.airconditioner.dto.GreeDeviceInfo;
import com.gree.airconditioner.models.GreeDevice;
import com.gree.airconditioner.models.GreeDeviceBinding;
import com.gree.airconditioner.dto.Command;
import com.gree.airconditioner.utils.CommandBuilder;
import com.gree.airconditioner.dto.packs.StatusResponsePack;
import com.gree.airconditioner.dto.status.GreeDeviceStatus;
import com.gree.airconditioner.dto.status.Switch;
import com.gree.airconditioner.dto.status.Temperature;
import com.gree.airconditioner.dto.status.TemperatureUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeService {
    private final GreeDeviceBinderService binderService;
    private final GreeCommunicationService communicationService;
    private final GreeDeviceFinderService deviceFinder;

    @Getter
    private final Set<GreeDevice> devices = new HashSet<>();

    public GreeDevice getDeviceByMac(final String mac) {
        Objects.requireNonNull(mac);
        return this.devices.stream()
                .filter(it -> mac.equalsIgnoreCase(it.getMacAddress()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Device with MAC " + mac + " not found!"));
    }

    public void changePowerStatus(final GreeDevice device, final boolean online) {
        log.info("Turning {} {}", online ? "on" : "off", device);
        final GreeDeviceBinding binding = binderService.getBinding(device);
        final GreeDeviceStatus status = new GreeDeviceStatus(online ? Switch.ON : Switch.OFF);
        final Command command = CommandBuilder.buildControlCommand(binding, status);
        communicationService.sendCommand(device, command);
    }

    public void changeTemperature(final GreeDevice device, final Integer temperature) {
        log.info("Setting temperature on {} to {}", device, temperature);
        final GreeDeviceBinding binding = binderService.getBinding(device);
        GreeDeviceStatus status = new GreeDeviceStatus();
        status.setTemperature(new Temperature(temperature, TemperatureUnit.CELSIUS));
        final Command command = CommandBuilder.buildControlCommand(binding, status);
        communicationService.sendCommand(device, command);
    }

    public GreeDeviceInfo getStatus(final GreeDevice device) {
        log.info("Fetching status of device {}", device);
        final GreeDeviceBinding binding = binderService.getBinding(device);
        final Command command = CommandBuilder.buildStatusCommand(binding);
        final String response = communicationService.sendCommand(device, command);
        final GreeDeviceStatus status = new StatusResponsePack(binding, response).asGreeDeviceStatus();
        return new GreeDeviceInfo(device, status);
    }

    @Scheduled(initialDelay = 2, fixedRate = 30*60, timeUnit = TimeUnit.SECONDS)
    private void addNetworkDevices() {
        this.devices.addAll(deviceFinder.searchAllNetworkInterfaces());
    }
}
