package com.gree.airconditioner.services;

import com.gree.airconditioner.dto.Command;
import com.gree.airconditioner.dto.CommandResponse;
import com.gree.airconditioner.dto.CommandType;
import com.gree.airconditioner.dto.packs.ScanResponsePack;
import com.gree.airconditioner.models.GreeDevice;
import com.gree.airconditioner.utils.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.gree.airconditioner.Application.OBJECT_MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeDeviceFinderService {
    private final DatagramSocket datagramSocket;

    @SneakyThrows
    public List<GreeDevice> searchAllNetworkInterfaces() {
        final List<GreeDevice> devices = new ArrayList<>();
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback()) {
                    continue; // Do not want to use the loopback interface
                }
                for (final InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    final InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                    if (broadcastAddress == null) {
                        continue;
                    }
                    devices.addAll(this.searchByAddress(broadcastAddress));
                }
            }
        } catch (SocketException e) {
            log.error("Searching devices failed!", e);
        }

        return devices;
    }

    @SneakyThrows
    public List<GreeDevice> searchByAddress(final String broadcastAddress) {
        return this.searchByAddress(InetAddress.getByName(broadcastAddress));
    }

    public List<GreeDevice> searchByAddress(final InetAddress broadcastAddress) throws IOException {
        log.info("Searching devices on broadcast {}, {}ms timeout", broadcastAddress.getHostAddress(), datagramSocket.getSoTimeout());

        final Command command = new Command(CommandType.SCAN);
        byte[] scanCommand = OBJECT_MAPPER.writeValueAsString(command).getBytes();

        final DatagramPacket sendPacket = new DatagramPacket(scanCommand, scanCommand.length, broadcastAddress, 7000);
        try {
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            log.error("Can't send packet", e);
        }

        final List<GreeDevice> devices = new ArrayList<>();

        byte[] receiveData = new byte[1024];
        boolean timeoutRecieved = false;
        while (!timeoutRecieved) {
            // Receive a response
            final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                datagramSocket.receive(receivePacket);
                final InetAddress address = receivePacket.getAddress();
                final Integer port = receivePacket.getPort();

                // Read the response
                final CommandResponse commandResponse = OBJECT_MAPPER.readValue(new String(receivePacket.getData()), CommandResponse.class);

                // If there was no pack, ignore the response
                if (commandResponse.getPack() == null) {
                    continue;
                }

                final String jsonPack = CryptoUtil.decryptPack(commandResponse.getPack());
                final ScanResponsePack pack = OBJECT_MAPPER.readValue(jsonPack, ScanResponsePack.class);
                final GreeDevice device = new GreeDevice();
                device.setName(pack.getFriendlyName());
                device.setVersion(pack.getVer());
                device.setMacAddress(pack.getMac());
                device.setAddress(address);
                device.setPort(port);
                devices.add(device);
                log.info("Found device {}", device);
            } catch (SocketTimeoutException e) {
                timeoutRecieved = true;
            }
        }
        log.info("Found {} devices on {}", devices.size(), broadcastAddress.getHostAddress());
        return devices;
    }
}
