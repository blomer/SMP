package xyz.leuo.smp.items.backpacks;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.leuo.smp.SMP;

import java.io.File;
import java.util.*;

public class BackpackManager {

    private SMP plugin;
    private File file;
    private FileConfiguration config;
    private @Getter Map<UUID, Backpack> backpacks;
    public BackpackManager(SMP plugin) {
        this.plugin = plugin;
        this.backpacks = new HashMap<>();

        ItemStack backpackItem = new ItemStack(Material.TRAPPED_CHEST);
        ItemMeta backpackMeta = backpackItem.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Your personal backpack.");

        backpackMeta.setDisplayName(ChatColor.GOLD + "Backpack");
        backpackMeta.setLore(lore);
        backpackItem.setItemMeta(backpackMeta);

        NamespacedKey backpackKey = new NamespacedKey(plugin, "backpack");
        ShapedRecipe backpackRecipe = new ShapedRecipe(backpackKey, backpackItem);
        backpackRecipe.shape(" L ", "LCL", " L ");
        backpackRecipe.setIngredient('L', Material.LEATHER);
        backpackRecipe.setIngredient('C', Material.CHEST);

        plugin.getSpecialItems().add(backpackItem);
        Bukkit.addRecipe(backpackRecipe);

        this.file = new File(plugin.getDataFolder(), "backpacks.yml");
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

        if(config.isConfigurationSection("backpacks")) {
            for(String s : config.getConfigurationSection("backpacks").getKeys(false)) {
                Backpack backpack = new Backpack(UUID.fromString(s));
                for(int i = 0; i < backpack.getInventory().getSize(); i++) {
                    String path = "backpacks." + s + "." + i;
                    if (config.isSet(path)) {
                        backpack.getInventory().setItem(i, config.getItemStack(path));
                    }
                }

                this.backpacks.put(backpack.getUuid(), backpack);
            }
        }
    }

    public Backpack find(Player player) {
        return find(player.getUniqueId());
    }

    public Backpack find(UUID uuid) {
        return this.backpacks.get(uuid);
    }

    public void export() {
        this.config.set("backpacks", " ");

        for(Backpack backpack : this.backpacks.values()) {
            backpack.export(this.config);
        }

        try {
            this.config.save(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        plugin.getLogger().info("Exported all backpacks back to file.");
    }

}
