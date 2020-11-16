package xyz.leuo.smp.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import xyz.leuo.smp.SMP;
import xyz.leuo.smp.profiles.Profile;

public class PlayerChangedWorldListener implements Listener {

    private SMP plugin;
    public PlayerChangedWorldListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().find(player);
        profile.update();
    }
}
