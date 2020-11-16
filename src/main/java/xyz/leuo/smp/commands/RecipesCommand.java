package xyz.leuo.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import xyz.leuo.gooey.action.Action;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;
import xyz.leuo.smp.SMP;

import java.util.Map;

public class RecipesCommand implements CommandExecutor {

    private SMP plugin;
    public RecipesCommand(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("recipes").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GUI gui = new GUI("Recipes", 9);

            for (ItemStack item : plugin.getSpecialItems()) {
                Button button = new Button(item, item.getItemMeta().getDisplayName());
                String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                button.setLore("&7Click to view the recipe for '" + name + "'.");
                button.setAction(new Action() {
                    @Override
                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                        GUI recipeGui = new GUI("Recipe for " + name, 27);
                        Recipe recipe = Bukkit.getRecipesFor(item).get(0);
                        if (recipe instanceof ShapedRecipe) {
                            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                            Map<Character, ItemStack> items = shapedRecipe.getIngredientMap();
                            int i = 3;
                            for (String row : shapedRecipe.getShape()) {
                                for (char c : row.toCharArray()) {
                                    ItemStack ri = items.get(c);
                                    if (ri != null) {
                                        Button rb = new Button(ri, null);
                                        recipeGui.setButton(i, rb);
                                    }
                                    i++;
                                }
                                i += 6;
                            }

                            Button back = new Button(Material.BARRIER, "&cBack");
                            back.setAction(new Action() {
                                @Override
                                public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                                    player.performCommand("recipes");
                                }
                            });

                            recipeGui.setButton(18, back);
                            recipeGui.open(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "No recipe found.");
                        }
                    }
                });

                gui.addButton(button);
            }
            gui.open(player);
        }

        return true;
    }
}
