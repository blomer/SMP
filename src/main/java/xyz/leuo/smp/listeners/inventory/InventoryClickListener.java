package xyz.leuo.smp.listeners.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import xyz.leuo.smp.SMP;

public class InventoryClickListener implements Listener {

    private SMP plugin;
    public InventoryClickListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();

        if(item != null && item.getType().equals(Material.TRAPPED_CHEST) && item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Backpack")) {
            if(inventory instanceof InventoryView) {
                InventoryView inventoryView = (InventoryView) inventory;
                if (inventoryView.getTitle().equals("Backpack")) {
                    event.setCancelled(true);
                    final ItemStack i = item.clone();
                    item.setType(Material.AIR);
                    player.setItemOnCursor(null);
                    player.getInventory().addItem(i);
                }
            }
        }
    }
}
