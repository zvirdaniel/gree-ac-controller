package cz.zvirdaniel.smarthome.models;


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
        return "MAC " + this.getMacAddress() + " at " + this.getAddress() + ":" + this.getPort();
    }
}
