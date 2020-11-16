package xyz.leuo.smp.items.backpacks;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public @Data class Backpack {

    private final UUID uuid;
    private Inventory inventory;

    public Backpack(UUID uuid) {
        this.uuid = uuid;
        this.inventory = Bukkit.createInventory(null, 9, "Backpack");
    }

    public Backpack(UUID uuid, Map<Integer, ItemStack> items) {
        this(uuid);
        for(Map.Entry<Integer, ItemStack> item : items.entrySet()) {
            inventory.setItem(item.getKey(), item.getValue());
        }
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
    }

    public void export(FileConfiguration config) {
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if(item != null && !item.getType().equals(Material.AIR)) {
                config.set("backpacks." + uuid + "." + i, item);
            }
        }
    }
}
