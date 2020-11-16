package xyz.leuo.smp.listeners.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.leuo.smp.SMP;

public class PlayerQuitListener implements Listener {

    private SMP plugin;
    public PlayerQuitListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " left the party early. D:");
    }
}
