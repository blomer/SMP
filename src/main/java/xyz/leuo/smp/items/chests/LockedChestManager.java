package xyz.leuo.smp.items.chests;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.leuo.smp.SMP;

import java.io.File;
import java.util.*;

public class LockedChestManager {

    private SMP plugin;
    private File file;
    private FileConfiguration config;
    private @Getter Map<UUID, LockedChest> lockedChests;
    public LockedChestManager(SMP plugin) {
        this.plugin = plugin;
        this.lockedChests = new HashMap<>();

        ItemStack keyItem = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta keyMeta = keyItem.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Tool for managing locked/unlocked chests.");
        lore.add(ChatColor.GRAY + "Right click on a chest to use!");

        keyMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Master Key");
        keyMeta.setLore(lore);
        keyItem.setItemMeta(keyMeta);

        NamespacedKey key = new NamespacedKey(plugin, "chest_key");
        ShapedRecipe recipe = new ShapedRecipe(key, keyItem);
        recipe.shape(" L ", "LCL", " L ");
        recipe.setIngredient('L', Material.GOLD_INGOT);
        recipe.setIngredient('C', Material.TRIPWIRE_HOOK);

        plugin.getSpecialItems().add(keyItem);
        Bukkit.addRecipe(recipe);

        this.file = new File(plugin.getDataFolder(), "lockedchests.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(this.config.isConfigurationSection("chests")) {
            for (String s : this.config.getConfigurationSection("chests").getKeys(false)) {
                UUID owner = UUID.fromString(s);
                if (this.config.getConfigurationSection("chests." + s) != null) {
                    for (String c : this.config.getConfigurationSection("chests." + s).getKeys(false)) {
                        UUID chestID = UUID.fromString(c);
                        Location location = this.config.getLocation("chests." + s + "." + c + ".location");
                        List<UUID> allowed = new ArrayList<>();
                        for (String a : this.config.getStringList("chests." + s + "." + c + ".allowed")) {
                            allowed.add(UUID.fromString(a));
                        }

                        LockedChest lockedChest = new LockedChest(owner, chestID, location);
                        lockedChest.setAllowed(allowed);
                        lockedChests.put(chestID, lockedChest);

                        if(config.isSet("chests." + s + "." + c + ".public")) {
                            lockedChest.setPublicChest(config.getBoolean("chests." + s + "." + c + ".public"));
                        }
                    }
                }
            }
        }
    }

    public LockedChest find(Location location) {
        return find((Chest) location.getBlock().getState());
    }

    public LockedChest find(Chest chest) {
        List<Chest> chests = new ArrayList<>();
        if(chest.getInventory() instanceof DoubleChestInventory) {
            DoubleChestInventory dci = (DoubleChestInventory) chest.getInventory();
            chests.add((Chest) dci.getLeftSide().getLocation().getBlock().getState());
            chests.add((Chest) dci.getRightSide().getLocation().getBlock().getState());
        } else {
            chests.add(chest);
        }

        for(LockedChest lockedChest : this.lockedChests.values()) {
            for (Chest ch : chests) {
                if (lockedChest.getLocation().equals(ch.getLocation())) {
                    return lockedChest;
                }
            }
        }

        return null;
    }

    public void add(LockedChest lockedChest) {
        this.lockedChests.put(lockedChest.getChestID(), lockedChest);
    }

    public void remove(LockedChest lockedChest) {
        this.lockedChests.remove(lockedChest.getChestID());
    }

    public void export() {
        this.config.set("chests", " ");
        for(LockedChest lockedChest : this.getLockedChests().values()) {
            lockedChest.export(this.config);
        }

        try {
            this.config.save(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        plugin.getLogger().info("Exported all locked chests back to file.");
    }
}
