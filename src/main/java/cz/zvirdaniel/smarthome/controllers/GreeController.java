package cz.zvirdaniel.smarthome.controllers;

import cz.zvirdaniel.smarthome.models.FanSpeed;
import cz.zvirdaniel.smarthome.models.GreeDeviceDetail;
import cz.zvirdaniel.smarthome.models.HorizontalSwingDirection;
import cz.zvirdaniel.smarthome.models.OperationMode;
import cz.zvirdaniel.smarthome.models.VerticalSwingDirection;
import cz.zvirdaniel.smarthome.services.GreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GreeController {
    private final GreeService service;

    @GetMapping("/devices")
    public List<GreeDeviceDetail> getDevices() {
        return service.getGreeDevices().stream()
                      .map(service::getStatus)
                      .collect(Collectors.toList());
    }

    @GetMapping("/device-status/{mac}")
    public GreeDeviceDetail getStatus(@PathVariable String mac) {
        return service.getStatus(service.getDeviceArray(mac)[0]);
    }

    @PatchMapping("/operation-mode")
    public List<GreeDeviceDetail> changeOperationMode(@RequestParam OperationMode mode) {
        service.changeOperationMode(mode);
        return this.getDevices();
    }

    @PatchMapping("/power")
    public List<GreeDeviceDetail> changePowerStatus(@RequestParam(required = false) String mac,
                                                    @RequestParam Boolean online) {
        service.changePowerStatus(mac, online);
        return this.getDevices();
    }

    @PatchMapping("/temperature")
    public List<GreeDeviceDetail> changeTemperature(@RequestParam(required = false) String mac,
                                                    @RequestParam Integer temperature) {
        service.changeTemperature(mac, temperature);
        return this.getDevices();
    }

    @PatchMapping("/fan-speed")
    public List<GreeDeviceDetail> changeFanSpeed(@RequestParam(required = false) String mac,
                                                 @RequestParam FanSpeed fanSpeed) {
        service.changeFanSpeed(mac, fanSpeed);
        return this.getDevices();
    }

    @PatchMapping("/swing")
    public List<GreeDeviceDetail> changeTemperature(@RequestParam(required = false) String mac,
                                                    @RequestParam HorizontalSwingDirection horizontalSwingDirection,
                                                    @RequestParam VerticalSwingDirection verticalSwingDirection) {
        service.changeSwing(mac, horizontalSwingDirection, verticalSwingDirection);
        return this.getDevices();
    }
}
