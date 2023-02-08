package cz.zvirdaniel.smarthome.services;

import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.models.GreeData;
import cz.zvirdaniel.smarthome.models.GreeDevice;
import cz.zvirdaniel.smarthome.models.GreeDeviceBinding;
import cz.zvirdaniel.smarthome.models.GreeType;
import cz.zvirdaniel.smarthome.models.contents.GreeBindContent;
import cz.zvirdaniel.smarthome.models.contents.GreeScanContent;
import cz.zvirdaniel.smarthome.models.requests.GreeBindRequest;
import cz.zvirdaniel.smarthome.models.requests.GreeRequest;
import cz.zvirdaniel.smarthome.models.requests.GreeScanRequest;
import cz.zvirdaniel.smarthome.utils.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeCommunicationService {
    private final DatagramSocket datagramSocket;

    private final Map<GreeDevice, GreeDeviceBinding> bindings = new HashMap<>();

    @Value("${gree.known-devices:}")
    public String knownDevicesConfig;

    public String sendRequest(GreeDevice device, GreeRequest request) {
        try {
            log.debug("Sending {} to {}", request, device);
            final String json = Application.OBJECT_MAPPER.writeValueAsString(request);
            datagramSocket.send(new DatagramPacket(json.getBytes(), json.getBytes().length, device.getAddress(), device.getPort()));
            byte[] receiveData = new byte[500];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.receive(receivePacket);
            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (Exception e) {
            throw new RuntimeException("Sending request " + request + " to device " + device + "failed!", e);
        }
    }

    public GreeDeviceBinding getBinding(GreeDevice device) {
        log.debug("Attempting to bind with {}", device.getAddress());
        if (bindings.containsKey(device)) {
            final GreeDeviceBinding binding = bindings.get(device);
            long bindingCreationTime = binding.getCreationDate().getTime();
            long nowTime = GregorianCalendar.getInstance().getTime().getTime();
            if (nowTime - bindingCreationTime < TimeUnit.MINUTES.toMillis(2)) {
                return binding;
            }
        }

        final GreeBindContent content;
        try {
            final GreeBindRequest request = new GreeBindRequest(device.getMacAddress());
            final GreeData response = Application.OBJECT_MAPPER.readValue(this.sendRequest(device, request), GreeData.class);
            final String rawContent = CryptoUtil.decryptContent(CryptoUtil.AES_General_Key, response.getEncryptedContent());
            content = Application.OBJECT_MAPPER.readValue(rawContent, GreeBindContent.class);
        } catch (IOException e) {
            throw new RuntimeException("Binding " + device + " failed!", e);
        }
        if (content.getType() != GreeType.BINDOK) {
            throw new RuntimeException("Binding " + device + " failed! Returned " + content.getType() + " with code " + content.getResponseCode());
        }

        log.debug("Bind with device at {} successful", device.getAddress().getHostAddress());
        final GreeDeviceBinding binding = new GreeDeviceBinding(device, content.getKey());
        bindings.put(device, binding);
        return binding;
    }

    @SneakyThrows
    public List<GreeDevice> searchAllNetworkDevices() {
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
                    devices.addAll(this.searchDevicesByAddress(broadcastAddress));
                }
            }
        } catch (SocketException e) {
            log.error("Searching devices failed!", e);
        }

        return devices;
    }

    private List<GreeDevice> searchDevicesByAddress(InetAddress broadcastAddress) throws IOException {
        log.info("Searching for devices on broadcast {}, {}ms timeout", broadcastAddress, datagramSocket.getSoTimeout());

        final GreeScanRequest request = new GreeScanRequest();
        try {
            final byte[] data = Application.OBJECT_MAPPER.writeValueAsString(request).getBytes();
            datagramSocket.send(new DatagramPacket(data, data.length, broadcastAddress, 7000));
        } catch (IOException e) {
            log.error("Can't send packet to {}", broadcastAddress, e);
        }

        final List<GreeDevice> devices = new ArrayList<>();

        byte[] receiveData = new byte[1024];
        boolean timeoutReceived = false;
        while (!timeoutReceived) {
            // Receive a response
            final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                datagramSocket.receive(receivePacket);
                final InetAddress address = receivePacket.getAddress();
                final Integer port = receivePacket.getPort();

                // Read the response
                final GreeData response = Application.OBJECT_MAPPER.readValue(new String(receivePacket.getData()), GreeData.class);

                // If there was no pack, ignore the response
                if (response.getEncryptedContent() == null) {
                    continue;
                }

                final String rawContent = CryptoUtil.decryptContent(CryptoUtil.AES_General_Key, response.getEncryptedContent());
                final GreeScanContent content = Application.OBJECT_MAPPER.readValue(rawContent, GreeScanContent.class);
                final GreeDevice device = new GreeDevice();
                device.setVersion(content.getVer());
                device.setMacAddress(content.getMac());
                device.setAddress(address);
                device.setPort(port);

                if (StringUtils.hasText(knownDevicesConfig)) {
                    final String[] knownDevices = knownDevicesConfig.split(";");
                    for (final var knownDevice : knownDevices) {
                        final String[] split = knownDevice.split("=");
                        if (split.length == 2) {
                            final String mac = split[0];
                            final String name = split[1];
                            if (device.getMacAddress().trim().equalsIgnoreCase(mac.trim())) {
                                device.setName(name);
                            }
                        }
                    }
                }

                devices.add(device);
                log.info("Found device {}", device);
            } catch (SocketTimeoutException e) {
                timeoutReceived = true;
            }
        }

        log.info("Found {} devices on broadcast {}", devices.size(), broadcastAddress);
        return devices;
    }
}
