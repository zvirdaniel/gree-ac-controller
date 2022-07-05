package com.gree.airconditioner.utils;

import com.gree.airconditioner.dto.Command;
import com.gree.airconditioner.dto.CommandType;
import com.gree.airconditioner.models.GreeDevice;
import com.gree.airconditioner.models.GreeDeviceBinding;
import com.gree.airconditioner.dto.packs.BindRequestPack;
import com.gree.airconditioner.dto.packs.ControlRequestPack;
import com.gree.airconditioner.dto.packs.StatusRequestPack;
import com.gree.airconditioner.dto.status.GreeDeviceStatus;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CommandBuilder {
    public static Command buildBindCommand(final GreeDevice device) {
        final Command command = new Command(CommandType.PACK);
        command.setCid("app");
        command.setI(1);
        command.setTcid(device.getMacAddress());
        command.setUid(1L);
        command.setPack(new BindRequestPack(device.getMacAddress()).encrypted());
        return command;
    }

    public static Command buildControlCommand(final GreeDeviceBinding binding, final GreeDeviceStatus status) {
        final Command command = new Command(CommandType.PACK);
        command.setCid("app");
        command.setI(0);
        command.setTcid(binding.getDevice().getMacAddress());
        command.setUid(0L);
        command.setPack(new ControlRequestPack(status).encrypted(binding.getAesKey()));
        return command;
    }

    public static Command buildStatusCommand(final GreeDeviceBinding binding) {
        final Command command = new Command(CommandType.PACK);
        command.setCid("app");
        command.setI(0);
        command.setTcid(binding.getDevice().getMacAddress());
        command.setUid(0L);
        command.setPack(new StatusRequestPack(binding.getDevice().getMacAddress()).encrypted(binding.getAesKey()));
        return command;
    }
}