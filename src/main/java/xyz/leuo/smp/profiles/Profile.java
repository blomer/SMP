package xyz.leuo.smp.profiles;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public @Data class Profile {

    private final UUID uuid;
    private Map<String, Location> trackerLocations;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.trackerLocations = new HashMap<>();
    }

    public String getPath() {
        return "profiles." + this.uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public void track(String world, Location location) {
        this.trackerLocations.put(world, location);
        this.update();
    }

    public void update() {
        Player player = this.getPlayer();
        World world = player.getWorld();
        Location location = trackerLocations.get(world.getName());
        if(location != null) {
            player.setCompassTarget(location);
        }
    }

    public void export(FileConfiguration config) {
        for(Map.Entry<String, Location> entry : this.getTrackerLocations().entrySet()) {
            config.set(this.getPath() + ".trackerlocations." + entry.getKey(), entry.getValue());
        }
    }
}
