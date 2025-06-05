package byrnes.jonathan.manager;

import java.util.HashSet;
import java.util.UUID;

public class DropLockManager {

    private final HashSet<UUID> dropLockedPlayers = new HashSet<>();

    public boolean isLocked(UUID uuid) {
        return dropLockedPlayers.contains(uuid);
    }

    public void toggleLock(UUID uuid) {
        if (!dropLockedPlayers.add(uuid)) {
            dropLockedPlayers.remove(uuid);
        }
    }

    public void setLocked(UUID uuid, boolean locked) {
        if (locked) dropLockedPlayers.add(uuid);
        else dropLockedPlayers.remove(uuid);
    }
}
