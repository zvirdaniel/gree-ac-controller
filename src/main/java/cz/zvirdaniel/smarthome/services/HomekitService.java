package cz.zvirdaniel.smarthome.services;

import cz.zvirdaniel.smarthome.configs.HomekitConfig;
import cz.zvirdaniel.smarthome.models.HomekitAuth;
import cz.zvirdaniel.smarthome.services.events.GreeConnectionEstablishedEvent;
import cz.zvirdaniel.smarthome.utils.QRtoConsole;
import io.github.hapjava.server.HomekitAccessoryCategories;
import io.github.hapjava.server.impl.HomekitRoot;
import io.github.hapjava.server.impl.HomekitServer;
import io.github.hapjava.server.impl.crypto.HAPSetupCodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomekitService implements DisposableBean {
    private final BuildProperties buildProperties;
    private final HomekitConfig homekitConfig;
    private final GreeService greeService;

    private HomekitServer homekitServer;

    @EventListener(GreeConnectionEstablishedEvent.class)
    public void startHomekitServer() throws IOException {
        if (!homekitConfig.isActive()) {
            log.error("Homekit disabled, ignoring {}", GreeConnectionEstablishedEvent.class.getSimpleName());
            return;
        }

        if (homekitServer != null) {
            log.error("Homekit server already initialized, ignoring {}", GreeConnectionEstablishedEvent.class.getSimpleName());
            return;
        }

        homekitServer = new HomekitServer(homekitConfig.getPort());
        final HomekitAuth auth = this.getHomekitAuth();
        final HomekitRoot bridge = this.createHomekitBridge(auth);

        QRtoConsole.printQR(HAPSetupCodeUtils.getSetupURI(
                auth.getPin().replace("-", ""),
                auth.getSetupId(),
                HomekitAccessoryCategories.BRIDGES
        ));

//        bridge.addAccessory(new MockSwitch());

        bridge.start();
    }

    private HomekitRoot createHomekitBridge(HomekitAuth auth) throws IOException {
        return homekitServer.createBridge(
                auth,
                homekitConfig.getBridgeLabel(),
                HomekitAccessoryCategories.BRIDGES,
                homekitConfig.getManufacturer(),
                homekitConfig.getModel(),
                homekitConfig.getSerialNumber(),
                buildProperties.getVersion(),
                homekitConfig.getHardwareRevision()
        );
    }

    @SneakyThrows
    private HomekitAuth getHomekitAuth() {
        final HomekitAuth auth = new HomekitAuth(
                homekitConfig.getPin(),
                HomekitServer.generateMac(),
                HomekitServer.generateSalt(),
                HomekitServer.generateKey(),
                HAPSetupCodeUtils.generateSetupId()
        );

        auth.setChangeListener(homekitAuth -> {
//            try {
//                log.info("Homekit auth changed, saving new configuration...");
//                final FileOutputStream fileOutputStream = new FileOutputStream(authFile);
//                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//                objectOutputStream.writeObject(homekitAuth);
//                objectOutputStream.flush();
//                objectOutputStream.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        });
        return auth;
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    @Override
    public void destroy() throws Exception {
        if (homekitServer != null) {
            log.info("Stopping homekit server");
            homekitServer.stop();
        }
    }
}
