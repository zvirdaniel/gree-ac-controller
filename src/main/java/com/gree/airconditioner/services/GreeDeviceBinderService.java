package com.gree.airconditioner.services;

import com.gree.airconditioner.models.GreeDevice;
import com.gree.airconditioner.dto.Command;
import com.gree.airconditioner.utils.CommandBuilder;
import com.gree.airconditioner.dto.CommandResponse;
import com.gree.airconditioner.dto.CommandType;
import com.gree.airconditioner.dto.packs.BindResponsePack;
import com.gree.airconditioner.models.GreeDeviceBinding;
import com.gree.airconditioner.utils.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gree.airconditioner.Application.OBJECT_MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeDeviceBinderService {
    private final GreeCommunicationService communicationService;

    private final Map<GreeDevice, GreeDeviceBinding> bindings = new HashMap<>();

    public GreeDeviceBinding getBinding(final GreeDevice device) {
        log.debug("Attempting to bind with {}", device.getAddress());
        final GreeDeviceBinding greeDeviceBinding = bindings.get(device);
        if (greeDeviceBinding != null) {
            long bindingCreationTime = greeDeviceBinding.getCreationDate().getTime();
            long nowTime = GregorianCalendar.getInstance().getTime().getTime();
            if (nowTime - bindingCreationTime < TimeUnit.MINUTES.toMillis(2)) {
                return greeDeviceBinding;
            }
        }
        final GreeDeviceBinding binding = this.sendBindCommand(device, CommandBuilder.buildBindCommand(device));
        bindings.put(device, binding);

        return binding;
    }


    private GreeDeviceBinding sendBindCommand(GreeDevice device, Command bindCommand) {
        final BindResponsePack responsePack;
        try {
            final String response = communicationService.sendCommand(device, bindCommand);
            final CommandResponse cmdResponse = OBJECT_MAPPER.readValue(response, CommandResponse.class);
            final String decryptedPack = CryptoUtil.decryptPack(cmdResponse.getPack());
            responsePack = OBJECT_MAPPER.readValue(decryptedPack, BindResponsePack.class);
        } catch (IOException e) {
            throw new RuntimeException("Binding " + device + " failed!", e);
        }
        if (!CommandType.BINDOK.getCode().equalsIgnoreCase(responsePack.getT())) {
            throw new RuntimeException("Binding " + device + " failed! Returned status: " + responsePack.getT());
        }

        log.debug("Bind with device at {} successful", device.getAddress().getHostAddress());
        return new GreeDeviceBinding(device, responsePack.getKey());
    }
}
