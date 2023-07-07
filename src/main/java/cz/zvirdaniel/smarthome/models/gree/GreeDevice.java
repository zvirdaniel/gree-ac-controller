package cz.zvirdaniel.smarthome.models.gree;

import java.net.InetAddress;

public record GreeDevice(String name, String version, String macAddress, InetAddress address, Integer port) {
    @Override
    public String toString() {
        return "MAC " + this.macAddress() + " at " + this.address() + ":" + this.port();
    }
}
