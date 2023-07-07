package cz.zvirdaniel.smarthome.controllers;

import cz.zvirdaniel.smarthome.models.gree.GreeDevice;
import cz.zvirdaniel.smarthome.models.gree.enums.FanSpeed;
import cz.zvirdaniel.smarthome.models.gree.enums.HorizontalSwingDirection;
import cz.zvirdaniel.smarthome.models.gree.enums.VerticalSwingDirection;
import cz.zvirdaniel.smarthome.services.GreeService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final GreeService greeService;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.servlet.context-path}")
    private String servletContextPath;

    @SneakyThrows
    @GetMapping("/ac")
    public String homePage(Model model) {
        if (!greeService.isConnected()) {
            return "loader";
        }
        final ArrayList<GreeDevice> devices = new ArrayList<>(greeService.getConnectedDevices());
        devices.sort(Comparator.comparing(GreeDevice::name));

        model.addAttribute("apiUrl", servletContextPath);
        model.addAttribute("devices", devices);
        model.addAttribute("fanSpeeds", FanSpeed.values());
        model.addAttribute("horizontalSwingDirections", HorizontalSwingDirection.values());
        model.addAttribute("verticalSwingDirections", VerticalSwingDirection.values());
        model.addAttribute("appName", appName);
        return "ac";
    }
}
