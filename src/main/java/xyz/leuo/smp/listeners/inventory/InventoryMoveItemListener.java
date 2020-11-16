package xyz.leuo.smp.listeners.inventory;

import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import xyz.leuo.smp.SMP;

public class InventoryMoveItemListener implements Listener {

    private SMP plugin;
    public InventoryMoveItemListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if(event.getSource().getLocation() != null && event.getSource().getLocation().getBlock().getState() instanceof Chest && plugin.getLockedChestManager().find((Chest) event.getSource().getLocation().getBlock().getState()) != null) {
            event.setCancelled(true);
        }
    }
}
