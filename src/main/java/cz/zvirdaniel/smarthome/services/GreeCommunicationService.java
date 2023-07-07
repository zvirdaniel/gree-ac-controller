package cz.zvirdaniel.smarthome.services;

import cz.zvirdaniel.smarthome.Application;
import cz.zvirdaniel.smarthome.configs.GreeConfig;
import cz.zvirdaniel.smarthome.configs.GreeConfig.GreeDeviceConfig;
import cz.zvirdaniel.smarthome.models.gree.GreeBindContent;
import cz.zvirdaniel.smarthome.models.gree.GreeBindRequest;
import cz.zvirdaniel.smarthome.models.gree.GreeBinding;
import cz.zvirdaniel.smarthome.models.gree.GreeData;
import cz.zvirdaniel.smarthome.models.gree.GreeDevice;
import cz.zvirdaniel.smarthome.models.gree.GreeRequest;
import cz.zvirdaniel.smarthome.models.gree.GreeScanContent;
import cz.zvirdaniel.smarthome.models.gree.GreeScanRequest;
import cz.zvirdaniel.smarthome.models.gree.enums.GreeType;
import cz.zvirdaniel.smarthome.services.events.GreeConnectionEstablishedEvent;
import cz.zvirdaniel.smarthome.utils.ControlUtil;
import cz.zvirdaniel.smarthome.utils.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreeCommunicationService {
    private final static int REFRESH_RATE_MINUTES = 30;

    private final GreeConfig config;
    private final DatagramSocket datagramSocket;
    private final ApplicationEventPublisher eventPublisher;

    private final Set<GreeDevice> connectedDevices = new HashSet<>();
    private final Map<String, GreeBinding> macAddressBindings = new ConcurrentHashMap<>();

    public boolean isConnected() {
        return config.getDevices().size() == connectedDevices.size();
    }

    public Set<GreeDevice> getConnectedDevices() {
        if (connectedDevices.isEmpty()) {
            if (config.getDevices().isEmpty()) {
                throw new RuntimeException("Devices are not configured!");
            }
            throw new RuntimeException("Configured devices are not reachable!");
        }

        return connectedDevices;
    }

    @Scheduled(fixedRate = REFRESH_RATE_MINUTES, timeUnit = TimeUnit.MINUTES)
    public void connectConfiguredDevices() {
        if (this.isConnected()) {
            return;
        }

        log.info("Attempting connection with {} configured devices", config.getDevices().size());
        final Set<GreeDevice> networkDevices = ControlUtil.retryBlock(
                "Gree Network Scanner",
                this::scanAllNetworkDevices,
                scannerDevices -> scannerDevices.size() == config.getDevices().size(),
                3, // 3 retries
                15 * 1000 // 10 seconds between scans
        );
        if (networkDevices == null) {
            log.error("Cannot reach all {} configured devices, retry in {} minutes", config.getDevices().size(), REFRESH_RATE_MINUTES);
            return;
        }

        connectedDevices.addAll(networkDevices);
        log.info("Connection established with all {} devices", connectedDevices.size());
        eventPublisher.publishEvent(new GreeConnectionEstablishedEvent(this));
    }

    public String sendRequest(GreeDevice device, GreeRequest request) {
        try {
            log.debug("Sending {} to {}", request, device);
            final String json = Application.OBJECT_MAPPER.writeValueAsString(request);
            datagramSocket.send(new DatagramPacket(json.getBytes(), json.getBytes().length, device.address(), device.port()));
            byte[] receiveData = new byte[500];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.receive(receivePacket);
            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (Exception e) {
            throw new RuntimeException("Sending request " + request + " to device " + device + "failed!", e);
        }
    }

    public GreeBinding getBinding(GreeDevice device) {
        log.debug("Binding with {}", device);
        if (macAddressBindings.containsKey(device.macAddress())) {
            final GreeBinding binding = macAddressBindings.get(device.macAddress());
            long bindingCreationTime = binding.getCreationDate().getTime();
            long nowTime = GregorianCalendar.getInstance().getTime().getTime();
            if (nowTime - bindingCreationTime < TimeUnit.MINUTES.toMillis(2)) {
                return binding;
            }
        }

        final GreeBindContent content;
        try {
            final GreeBindRequest request = new GreeBindRequest(device.macAddress());
            final GreeData response = Application.OBJECT_MAPPER.readValue(this.sendRequest(device, request), GreeData.class);
            final String rawContent = CryptoUtil.decryptContent(CryptoUtil.AES_General_Key, response.getEncryptedContent());
            content = Application.OBJECT_MAPPER.readValue(rawContent, GreeBindContent.class);
        } catch (IOException e) {
            throw new RuntimeException("Binding " + device + " failed!", e);
        }
        if (content.getType() != GreeType.BINDOK) {
            throw new RuntimeException("Binding " + device + " failed! Returned " + content.getType() + " with code " + content.getResponseCode());
        }

        log.debug("Bind with device at {} successful", device.address().getHostAddress());
        final GreeBinding binding = new GreeBinding(device, content.getKey());
        macAddressBindings.put(device.macAddress(), binding);
        return binding;
    }

    @SneakyThrows
    public Set<GreeDevice> scanAllNetworkDevices() {
        final Set<GreeDevice> devices = new HashSet<>();
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
                    log.info("Scanning for devices on broadcast {}, {}ms timeout", broadcastAddress, datagramSocket.getSoTimeout());

                    final GreeScanRequest request = new GreeScanRequest();
                    try {
                        final byte[] data = Application.OBJECT_MAPPER.writeValueAsString(request).getBytes();
                        datagramSocket.send(new DatagramPacket(data, data.length, broadcastAddress, 7000));
                    } catch (IOException e) {
                        log.error("Can't send packet to {}", broadcastAddress, e);
                    }

                    int counter = 0;
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
                            final String name = Optional.ofNullable(content.getMac())
                                                        .map(it -> config.getDevices().get(it))
                                                        .map(GreeDeviceConfig::name)
                                                        .orElse(null);

                            final var device = new GreeDevice(name, content.getVer(), content.getMac(), address, port);
                            devices.add(device);
                            counter++;
                            log.info("Scanner found device {}", device);
                        } catch (SocketTimeoutException e) {
                            timeoutReceived = true;
                        }
                    }

                    log.info("Scanner found {} devices on broadcast {}", counter, broadcastAddress);
                }
            }
        } catch (SocketException e) {
            log.error("Scanning devices failed!", e);
        }

        return devices;
    }
}
