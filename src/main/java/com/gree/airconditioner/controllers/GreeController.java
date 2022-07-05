package com.gree.airconditioner.controllers;

import com.gree.airconditioner.dto.GreeDeviceInfo;
import com.gree.airconditioner.models.GreeDevice;
import com.gree.airconditioner.dto.status.GreeDeviceStatus;
import com.gree.airconditioner.services.GreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GreeController {
    private final GreeService service;

    @GetMapping("/devices")
    public List<GreeDeviceInfo> getDevices() {
        return service.getDevices().stream()
                .map(service::getStatus)
                .collect(Collectors.toList());
    }

    @GetMapping("/device-status/{mac}")
    public GreeDeviceInfo getStatus(final @PathVariable String mac) {
        return service.getStatus(this.service.getDeviceByMac(mac));
    }

    @PatchMapping("/power/{mac}")
    public void changePowerStatus(final @PathVariable String mac,
                                  final @RequestParam Boolean online) {
        service.changePowerStatus(service.getDeviceByMac(mac), online);
    }

    @PatchMapping("/temperature/{mac}")
    public void changeTemperature(final @PathVariable String mac,
                                  final @RequestParam Integer temperature) {
        service.changeTemperature(service.getDeviceByMac(mac), temperature);
    }
}
