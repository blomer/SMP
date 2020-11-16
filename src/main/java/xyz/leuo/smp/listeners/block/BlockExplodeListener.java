package xyz.leuo.smp.listeners.block;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import xyz.leuo.smp.SMP;
import xyz.leuo.smp.items.chests.LockedChest;

import java.util.ArrayList;
import java.util.List;

public class BlockExplodeListener implements Listener {

    private SMP plugin;
    public BlockExplodeListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        List<Block> remove = new ArrayList<>();
        for(Block block : event.blockList()) {
            if(block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                LockedChest lockedChest = plugin.getLockedChestManager().find(chest);
                if(lockedChest != null) {
                    remove.add(block);
                }
            }
        }

        event.blockList().removeAll(remove);
    }
}
