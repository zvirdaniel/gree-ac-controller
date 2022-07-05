package com.gree.airconditioner.models;


import lombok.Data;

import java.net.InetAddress;

@Data
public class GreeDevice {
    private String name;
    private String version;
    private String macAddress;
    private InetAddress address;
    private Integer port;

    @Override
    public String toString() {
        return this.getName() + " at " + this.getAddress() + ":" + this.getPort() + " with MAC " + this.getMacAddress();
    }
}
