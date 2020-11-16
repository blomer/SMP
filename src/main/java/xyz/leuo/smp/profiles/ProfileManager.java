package xyz.leuo.smp.profiles;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.leuo.smp.SMP;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private SMP plugin;
    private File file;
    private FileConfiguration config;
    private @Getter Map<UUID, Profile> profiles;
    public ProfileManager(SMP plugin) {
        this.plugin = plugin;
        this.profiles = new HashMap<>();

        this.file = new File(plugin.getDataFolder(), "profiles.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, ()-> {
            for(Player p : Bukkit.getOnlinePlayers()) {
                this.find(p.getUniqueId()).update();
            }
        }, 20, 20);
    }

    public Profile find(Player player) {
        return find(player.getUniqueId());
    }

    public Profile find(UUID uuid) {
        Profile profile = this.profiles.get(uuid);

        if(profile == null) {
            profile = this.importFromConfig(uuid);
        }

        return profile;
    }

    public Profile importFromConfig(UUID uuid) {
        Profile profile = new Profile(uuid);
        if(config.isConfigurationSection("profiles." + uuid + ".trackerlocations")) {
            String path = profile.getPath() + ".trackerlocations";
            for(String s : config.getConfigurationSection(path).getKeys(false)) {
                profile.getTrackerLocations().put(s, config.getLocation(path + "." + s));
            }
        }

        this.profiles.put(uuid, profile);
        return profile;
    }

    public void export() {
        for(Profile profile : this.profiles.values()) {
            profile.export(this.config);
        }

        try {
            this.config.save(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        plugin.getLogger().info("Exported all profiles back to file.");
    }
}
