package cz.zvirdaniel.smarthome.controllers;

import cz.zvirdaniel.smarthome.models.gree.GreeDeviceDetail;
import cz.zvirdaniel.smarthome.models.gree.enums.FanSpeed;
import cz.zvirdaniel.smarthome.models.gree.enums.HorizontalSwingDirection;
import cz.zvirdaniel.smarthome.models.gree.enums.OperationMode;
import cz.zvirdaniel.smarthome.models.gree.enums.VerticalSwingDirection;
import cz.zvirdaniel.smarthome.services.GreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/climate")
public class ClimateController {
    private final GreeService greeService;

    @GetMapping("/gree/network-devices")
    public List<GreeDeviceDetail> scanGreeNetworkDevices() {
        return greeService.scanNetworkDevices().stream()
                          .map(greeService::getStatus)
                          .collect(Collectors.toList());
    }

    @GetMapping("/gree/connected-devices")
    public List<GreeDeviceDetail> getGreeConnectedDevices() {
        return greeService.getConnectedDevices().stream()
                          .map(greeService::getStatus)
                          .collect(Collectors.toList());
    }

    @GetMapping("/gree/connected-devices/device-status/{mac}")
    public GreeDeviceDetail getGreeStatus(@PathVariable String mac) {
        return greeService.getStatus(greeService.getTargetDevices(mac).stream().findFirst().orElseThrow());
    }

    @PatchMapping("/gree/connected-devices/operation-mode")
    public List<GreeDeviceDetail> changeGreeOperationMode(@RequestParam OperationMode mode) {
        return greeService.changeOperationMode(mode);
    }

    @PatchMapping("/gree/connected-devices/power")
    public List<GreeDeviceDetail> changeGreePowerStatus(@RequestParam(required = false) String mac,
                                                        @RequestParam Boolean online) {
        return greeService.changePowerStatus(mac, online);
    }

    @PatchMapping("/gree/connected-devices/temperature")
    public List<GreeDeviceDetail> changeGreeTemperature(@RequestParam(required = false) String mac,
                                                        @RequestParam Integer temperature) {
        return greeService.changeTemperature(mac, temperature);
    }

    @PatchMapping("/gree/connected-devices/fan-speed")
    public List<GreeDeviceDetail> changeGreeFanSpeed(@RequestParam(required = false) String mac,
                                                     @RequestParam FanSpeed fanSpeed) {
        return greeService.changeFanSpeed(mac, fanSpeed);
    }

    @PatchMapping("/gree/connected-devices/swing")
    public List<GreeDeviceDetail> changeGreeSwingDirections(@RequestParam(required = false) String mac,
                                                            @RequestParam HorizontalSwingDirection horizontalSwingDirection,
                                                            @RequestParam VerticalSwingDirection verticalSwingDirection) {
        return greeService.changeSwing(mac, horizontalSwingDirection, verticalSwingDirection);
    }
}
