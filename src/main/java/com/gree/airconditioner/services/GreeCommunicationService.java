package com.gree.airconditioner.services;

import com.gree.airconditioner.dto.Command;
import com.gree.airconditioner.models.GreeDevice;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.gree.airconditioner.Application.OBJECT_MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeCommunicationService {
    private final DatagramSocket datagramSocket;

    public String sendCommand(final GreeDevice device, final Command command) {
        try {
            log.debug("Sending {} to {}", command, device);
            final String json = OBJECT_MAPPER.writeValueAsString(command);
            final InetAddress address = device.getAddress();
            final Integer port = device.getPort();
            final DatagramPacket datagram = new DatagramPacket(json.getBytes(), json.getBytes().length, address, port);
            datagramSocket.send(datagram);
            byte[] receiveData = new byte[500];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.receive(receivePacket);
            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (Exception e) {
            throw new RuntimeException("Sending command " + command + " to device " + device + "failed!", e);
        }
    }
}
