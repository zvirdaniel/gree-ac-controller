package cz.zvirdaniel.smarthome.controllers;

import cz.zvirdaniel.smarthome.models.GreeDevice;
import cz.zvirdaniel.smarthome.models.GreeDeviceDetail;
import cz.zvirdaniel.smarthome.models.HorizontalSwingDirection;
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
		return service.getDevices().stream()
		              .map(service::getStatus)
		              .collect(Collectors.toList());
	}

	@GetMapping("/device-status/{mac}")
	public GreeDeviceDetail getStatus(@PathVariable String mac) {
		return service.getStatus(this.service.getDeviceByMac(mac));
	}

	@PatchMapping("/power/{mac}")
	public GreeDeviceDetail changePowerStatus(@PathVariable String mac,
	                                          @RequestParam Boolean online) {
		final GreeDevice device = service.getDeviceByMac(mac);
		service.changePowerStatus(device, online);
		return service.getStatus(device);
	}

	@PatchMapping("/temperature/{mac}")
	public GreeDeviceDetail changeTemperature(@PathVariable String mac,
	                                          @RequestParam Integer temperature) {
		final GreeDevice device = service.getDeviceByMac(mac);
		service.changeTemperature(device, temperature);
		return service.getStatus(device);
	}

	@PatchMapping("/swing/{mac}")
	public GreeDeviceDetail changeTemperature(@PathVariable String mac,
	                                          @RequestParam HorizontalSwingDirection horizontalSwingDirection,
	                                          @RequestParam VerticalSwingDirection verticalSwingDirection) {
		final GreeDevice device = service.getDeviceByMac(mac);
		service.changeSwing(device, horizontalSwingDirection, verticalSwingDirection);
		return service.getStatus(device);
	}
}
