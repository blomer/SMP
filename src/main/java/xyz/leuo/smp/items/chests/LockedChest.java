package xyz.leuo.smp.items.chests;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.leuo.smp.SMP;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public @Data class LockedChest {
    private UUID owner;
    private final UUID chestID;
    private boolean publicChest;
    private Location location;
    private List<UUID> allowed;

    public LockedChest(UUID owner, UUID chestID, Location location) {
        this.owner = owner;
        this.chestID = chestID;
        this.location = location;
        this.allowed = new ArrayList<>();
    }

    public boolean canOpen(Player player) {
        return canOpen(player.getUniqueId());
    }

    public boolean canOpen(UUID uuid) {
        return this.owner.toString().equals(uuid.toString()) || this.allowed.contains(uuid) || this.publicChest;
    }

    public boolean hasAccess (Player player) {
        return hasAccess(player.getUniqueId());
    }

    public boolean hasAccess (UUID uuid) {
        return owner.toString().equals(uuid.toString());
    }

    public String getConfigLocation() {
        return "chests." + owner + "." + chestID;
    }

    public String getOwnerName() {
        return Bukkit.getOfflinePlayer(this.owner).getName();
    }

    public void export(FileConfiguration config) {
        List<String> allowed = new ArrayList<>();
        for(UUID uuid : this.allowed) {
            allowed.add(uuid.toString());
        }

        config.set(this.getConfigLocation() + ".location", location);
        config.set(this.getConfigLocation() + ".public", publicChest);
        config.set(this.getConfigLocation() + ".allowed", allowed);
    }

    public void grant(UUID uuid) {
        this.allowed.add(uuid);
    }

    public void revoke(UUID uuid) {
        this.allowed.remove(uuid);
    }

    public void transfer(UUID uuid) {
        this.owner = uuid;
    }

    public void move(Location location) {
        this.location = location;
    }

    public void unlock() {
        SMP.instance.getLockedChestManager().remove(this);
    }
}
