package com.gree.airconditioner.dto;

import com.gree.airconditioner.dto.status.GreeDeviceStatus;
import com.gree.airconditioner.models.GreeDevice;
import lombok.Data;

@Data
public class GreeDeviceInfo {
    private String name;
    private String version;
    private String macAddress;
    private String address;
    private Integer port;
    private GreeDeviceStatus status;

    public GreeDeviceInfo(final GreeDevice device, final GreeDeviceStatus status) {
        this.name = device.getName();
        this.version = device.getVersion();
        this.macAddress = device.getMacAddress();
        this.address = device.getAddress().getHostAddress();
        this.port = device.getPort();
        this.status = status;
    }
}
