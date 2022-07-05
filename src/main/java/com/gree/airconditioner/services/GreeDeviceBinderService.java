package com.gree.airconditioner.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        GreeDeviceBinding greeDeviceBinding = bindings.get(device);
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
        final String response = communicationService.sendCommand(device, bindCommand);
        final BindResponsePack responsePack = this.decrypt(response);
        if (responsePack == null) {
            return null;
        }
        if (CommandType.BINDOK.getCode().equalsIgnoreCase(responsePack.getT())) {
            log.debug("Bind with device at {} successful", device.getAddress().getHostAddress());
            return new GreeDeviceBinding(device, responsePack.getKey());
        } else {
            throw new RuntimeException("Binding " + device + " failed!");
        }
    }

    private BindResponsePack decrypt(final String input) {
        try {
            final CommandResponse response = OBJECT_MAPPER.readValue(input, CommandResponse.class);
            final String encryptedPack = response.getPack();
            final String decryptedPack = CryptoUtil.decryptPack(encryptedPack);
            return OBJECT_MAPPER.readValue(decryptedPack, BindResponsePack.class);
        } catch (IOException e) {
            log.error("Can't map binding response to command response", e);
            return null;
        }
    }
}
