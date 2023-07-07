package cz.zvirdaniel.smarthome.models;

import io.github.hapjava.server.HomekitAuthInfo;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Data
public class HomekitAuth implements Serializable, HomekitAuthInfo {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter(AccessLevel.PRIVATE)
    private transient Consumer<HomekitAuth> changeListener;

    private final String pin;
    private final String mac;
    private final BigInteger salt;
    private final byte[] privateKey;
    private final String setupId;
    private final Map<String, HomekitUser> users = new ConcurrentHashMap<>();

    public record HomekitUser(String username, byte[] publicKey, boolean isAdmin) {}

    /**
     * Called during the pairing process, you should store the user and public key in a manner that
     * the public key can later be retrieved using {@link #getUserPublicKey(String)}. This must be
     * stored in a persistent store as pairing will need to be reset if the information is lost.
     *
     * @param username  the iOS device's username. The value will not be meaningful to anything but
     *                  iOS.
     * @param publicKey the iOS device's public key.
     * @param isAdmin   if the user is an admin, authorized to and/remove other users
     */
    @Override
    public void createUser(String username, byte[] publicKey, boolean isAdmin) {
        if (!this.users.containsKey(username)) {
            this.users.put(username, new HomekitUser(username, publicKey, isAdmin));
            log.info("Homekit: added pairing for {} ({})", username, isAdmin ? "admin" : "non-admin");
            if (changeListener != null) {
                changeListener.accept(this);
            }
        } else {
            log.info("Homekit: pairing for {} already exists", username);
        }
    }

    /**
     * Deprecated method to add a user, assuming all users are admins.
     *
     * <p>At least one of the createUser methods must be implemented.
     *
     * @param username  the iOS device's username.
     * @param publicKey the iOS device's public key.
     */
    @Override
    public void createUser(String username, byte[] publicKey) {
        this.createUser(username, publicKey, true);
    }

    /**
     * Called when an iOS device needs to remove an existing pairing. Subsequent calls to {@link
     * #getUserPublicKey(String)} for this username return null.
     *
     * @param username the username to delete from the persistent store.
     */
    @Override
    public void removeUser(String username) {
        this.users.remove(username);
        log.info("Homekit: removed pairing for {}", username);
        if (changeListener != null) {
            changeListener.accept(this);
        }
    }

    /**
     * List all users which have been authenticated.
     *
     * @return the previously stored list of users.
     */
    @Override
    public Collection<String> listUsers() {
        return this.users.keySet();
    }

    /**
     * Called when an already paired iOS device is re-connecting. The public key returned by this
     * method will be compared with the signature of the pair verification request to validate the
     * device.
     *
     * @param username the username of the iOS device to retrieve the public key for.
     * @return the previously stored public key for this user.
     */
    @Override
    public byte[] getUserPublicKey(String username) {
        return Optional.ofNullable(this.users.get(username))
                       .map(HomekitUser::publicKey)
                       .orElse(null);
    }

    /**
     * Determine if the specified user is an admin.
     *
     * @param username the username of the iOS device to retrieve permissions for.
     * @return the previously stored permissions.
     */
    @Override
    public boolean userIsAdmin(String username) {
        return Optional.ofNullable(this.users.get(username))
                       .map(HomekitUser::isAdmin)
                       .orElse(false);
    }

    /**
     * Called to check if a user has been created. The homekit accessory advertises whether the
     * accessory has already been paired. At this time, it's unclear whether multiple users can be
     * created, however it is known that advertising as unpaired will break in iOS 9. The default
     * value has been provided to maintain API compatibility for implementations targeting iOS 8.
     *
     * @return whether a user has been created and stored
     */
    @Override
    public boolean hasUser() {
        return !this.users.isEmpty();
    }
}