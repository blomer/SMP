package xyz.leuo.smp;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.leuo.gooey.Gooey;
import xyz.leuo.smp.commands.RecipesCommand;
import xyz.leuo.smp.commands.tracker.TrackCommand;
import xyz.leuo.smp.items.backpacks.BackpackManager;
import xyz.leuo.smp.items.chests.LockedChestManager;
import xyz.leuo.smp.commands.CoordinatesCommand;
import xyz.leuo.smp.listeners.block.BlockBreakListener;
import xyz.leuo.smp.listeners.block.BlockExplodeListener;
import xyz.leuo.smp.listeners.entity.EntityExplodeListener;
import xyz.leuo.smp.listeners.entity.EntitySpawnListener;
import xyz.leuo.smp.listeners.inventory.InventoryClickListener;
import xyz.leuo.smp.listeners.inventory.InventoryMoveItemListener;
import xyz.leuo.smp.listeners.player.PlayerChangedWorldListener;
import xyz.leuo.smp.listeners.player.PlayerInteractListener;
import xyz.leuo.smp.listeners.player.PlayerJoinListener;
import xyz.leuo.smp.listeners.player.PlayerQuitListener;
import xyz.leuo.smp.profiles.ProfileManager;
import xyz.leuo.smp.tasks.SleepTask;

import java.util.ArrayList;
import java.util.List;

public class SMP extends JavaPlugin {

    public static SMP instance;

    private @Getter List<ItemStack> specialItems;

    private @Getter BackpackManager backpackManager;
    private @Getter LockedChestManager lockedChestManager;
    private @Getter ProfileManager profileManager;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        new Gooey(this);

        this.specialItems = new ArrayList<>();

        this.backpackManager = new BackpackManager(this);
        this.lockedChestManager = new LockedChestManager(this);
        this.profileManager = new ProfileManager(this);

        registerCommands();
        registerListeners();
        startTasks();
    }

    @Override
    public void onDisable() {
        this.backpackManager.export();
        this.lockedChestManager.export();
        this.profileManager.export();

        this.saveConfig();

        instance = null;
    }

    public void registerCommands() {
        new CoordinatesCommand(this);
        new RecipesCommand(this);
        new TrackCommand(this);
    }

    public void registerListeners() {
        new BlockBreakListener(this);
        new BlockExplodeListener(this);

        new EntityExplodeListener(this);
        new EntitySpawnListener(this);

        new InventoryClickListener(this);
        new InventoryMoveItemListener(this);

        new PlayerChangedWorldListener(this);
        new PlayerInteractListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }

    public void startTasks() {
        Bukkit.getScheduler().runTaskTimer(this, new SleepTask(this), 2, 2);
    }

    public boolean isSpecial(ItemStack item) {
        for(ItemStack i : this.specialItems) {
            if(item.getItemMeta() != null && item.getItemMeta().equals(i.getItemMeta()) && item.getType() == i.getType()) {
                return true;
            }
        }

        return false;
    }
}
