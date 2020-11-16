package xyz.leuo.smp.listeners.block;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.DoubleChestInventory;
import xyz.leuo.smp.SMP;
import xyz.leuo.smp.items.chests.LockedChest;

public class BlockBreakListener implements Listener {

    private SMP plugin;
    public BlockBreakListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            LockedChest lockedChest = plugin.getLockedChestManager().find(chest);
            if(lockedChest != null) {
                if(lockedChest.hasAccess(player)) {
                    if(chest.getInventory() instanceof DoubleChestInventory) {
                        DoubleChestInventory dci = (DoubleChestInventory) chest.getInventory();
                        if(dci.getRightSide().getLocation() == lockedChest.getLocation()) {
                            lockedChest.move(dci.getLeftSide().getLocation());
                        } else {
                            lockedChest.move(dci.getRightSide().getLocation());
                        }
                    } else {
                        lockedChest.unlock();
                        player.sendMessage(ChatColor.RED + "You have broken one of your locked chests.");
                    }
                } else {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break " + lockedChest.getOwnerName() + "'s chest.");
                }
            }
        }
    }
}
