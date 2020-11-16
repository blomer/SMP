package xyz.leuo.smp.listeners.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import xyz.leuo.smp.SMP;

public class EntitySpawnListener implements Listener {

    private SMP plugin;
    public EntitySpawnListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Phantom) {
            event.setCancelled(true);
        }
    }
}
