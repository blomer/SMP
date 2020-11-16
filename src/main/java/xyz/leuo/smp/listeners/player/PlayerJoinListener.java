package xyz.leuo.smp.listeners.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.leuo.smp.SMP;
import xyz.leuo.smp.items.backpacks.Backpack;
import xyz.leuo.smp.profiles.Profile;

public class PlayerJoinListener implements Listener {

    private SMP plugin;
    public PlayerJoinListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().find(player);
        profile.update();

        if(plugin.getBackpackManager().find(player) == null) {
            plugin.getBackpackManager().getBackpacks().put(player.getUniqueId(), new Backpack(player.getUniqueId()));
        }

        event.setJoinMessage(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " came to the party!");
    }
}
