package xyz.leuo.smp.listeners.player;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;
import xyz.leuo.gooey.gui.PaginatedGUI;
import xyz.leuo.smp.SMP;
import xyz.leuo.smp.items.backpacks.Backpack;
import xyz.leuo.smp.items.chests.LockedChest;
import xyz.leuo.smp.profiles.Profile;

import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private SMP plugin;
    public PlayerInteractListener(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().find(player);
        Block block = event.getClickedBlock() == null ? null : event.getClickedBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemStack offItem = player.getInventory().getItemInOffHand();

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if(plugin.isSpecial(item) || plugin.isSpecial(offItem)) {
                event.setCancelled(true);
            }

            if(item.getType().equals(Material.TRAPPED_CHEST) && item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Backpack")) {
                Backpack backpack = plugin.getBackpackManager().find(player);
                if(backpack != null) {
                    backpack.open(player);
                    return;
                }
            } else if(item.getType().equals(Material.COMPASS)) {
                GUI gui = new GUI("Tracker", 9);
                Location location = player.getCompassTarget();

                Button currentLocation = new Button(Material.MAP, 1, "&6&lCurrent Location");
                currentLocation.setLore("&7Sets your tracker location to your current location.");
                currentLocation.setCloseOnClick(true);
                currentLocation.setAction(new xyz.leuo.gooey.action.Action() {
                    @Override
                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                        profile.track(player.getWorld().getName(), player.getLocation());
                        player.setCompassTarget(player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Your tracker is now set to your current location.");
                    }
                });

                Button worldSpawnLocation = new Button(Material.GRASS_BLOCK, 1, "&a&lWorld's Spawn Location");
                worldSpawnLocation.setLore(
                        "&7Sets your tracker location to the world's spawn.",
                        "&7The world you are in is where the tracker ",
                        "&7will get the spawn location from.");
                worldSpawnLocation.setCloseOnClick(true);
                worldSpawnLocation.setAction(new xyz.leuo.gooey.action.Action() {
                    @Override
                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                        profile.track(player.getWorld().getName(), player.getWorld().getSpawnLocation());
                        player.setCompassTarget(player.getWorld().getSpawnLocation());
                        player.sendMessage(ChatColor.GREEN + "Your tracker is now set to the world's spawn location.");
                    }
                });

                Button bedLocation = new Button(Material.RED_BED, 1, "&c&lBed's Location");
                bedLocation.setLore("&7Sets your tracker location to your bed's location.");
                bedLocation.setCloseOnClick(true);
                bedLocation.setAction(new xyz.leuo.gooey.action.Action() {
                    @Override
                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                        if (player.getBedSpawnLocation() != null) {
                            profile.track(player.getBedSpawnLocation().getWorld().getName(), profile.getPlayer().getBedSpawnLocation());
                            player.setCompassTarget(player.getBedSpawnLocation());
                            player.sendMessage(ChatColor.GREEN + "Your tracker is now set to your bed's location.");
                        } else {
                            player.sendMessage(ChatColor.RED + "Your bed was obstructed.");
                        }
                    }
                });

                Button warning = new Button(Material.BARRIER, 1, "&4&lTracker Broken");
                warning.setLore("&cYour tracker in this dimension is broken.", "&7You can still use some functions of the tracker.");

                Button shareLocation = new Button(Material.COMPASS, 1, "&5&lShare Location");
                shareLocation.setLore("&7Share your location with the whole server.");
                shareLocation.setAction(new xyz.leuo.gooey.action.Action() {
                    @Override
                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                        player.performCommand("coordinates");
                    }
                });

                Button setLocation = new Button(Material.NETHER_STAR, 1, "&a&lCurrent Tracker Location");
                setLocation.setLore("&aX: &f" + (int) Math.round(location.getX()),
                        "&aY: &f" + (int) Math.round(location.getY()),
                        "&aZ: &f" + (int) Math.round(location.getZ()),
                        "&aWorld: &f" + StringUtils.capitalize(location.getWorld().getName().replace('_', ' ')));

                gui.addButtons(currentLocation, worldSpawnLocation);

                if(player.getBedSpawnLocation() != null && player.getBedSpawnLocation().getWorld().equals(player.getWorld())) {
                    gui.addButtons(bedLocation);
                }

                if(!player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                    gui.setButton(4, warning);
                }

                gui.setButton(7, shareLocation);
                gui.setButton(8, setLocation);
                gui.open(player);
            }
        }

        if(block != null) {
            if(block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                LockedChest lockedChest = plugin.getLockedChestManager().find(chest);
                if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if(lockedChest != null) {
                        String owner = lockedChest.getOwnerName();
                        if (item.getType().equals(Material.TRIPWIRE_HOOK) && item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Master Key")) {
                            if (lockedChest.hasAccess(player)) {
                                GUI gui = new GUI("Locked Chest Manager", 9);

                                Button unlock = new Button(Material.LEAD, 1, "&4&lUnlock Chest");
                                unlock.setLore("&7Click to unlock this chest.");
                                unlock.setCloseOnClick(true);
                                unlock.setAction((player1, gui1, button, inventoryClickEvent) -> {
                                    lockedChest.unlock();
                                    player1.sendMessage(ChatColor.GREEN + "Successfully unlocked this chest.");
                                    player1.playSound(player1.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                });

                                Button privacy = new Button(Material.ARMOR_STAND, "&d&lChest Privacy");
                                privacy.setLore("&dCurrent Setting: &f" + (lockedChest.isPublicChest() ? "Public" : "Private"),
                                        "&7If a chest is public, anyone can open it.");
                                privacy.setCloseOnClick(true);
                                privacy.setAction(new xyz.leuo.gooey.action.Action() {
                                    @Override
                                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                                        lockedChest.setPublicChest(!lockedChest.isPublicChest());
                                        player.sendMessage(ChatColor.GREEN + "This chest is now " + (lockedChest.isPublicChest() ? "public." : "private."));
                                    }
                                });

                                Button grant = new Button(Material.PLAYER_HEAD, 1, "&6&lGrant Access");
                                grant.setLore("&7Grant someone access to your locked chest.");
                                grant.setAction(new xyz.leuo.gooey.action.Action() {
                                    @Override
                                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                                        PaginatedGUI paginatedGUI = new PaginatedGUI("Grant Access", 36);
                                        for(Player p : Bukkit.getOnlinePlayers()) {
                                            if(!lockedChest.canOpen(p)) {
                                                ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
                                                SkullMeta skull = (SkullMeta) item.getItemMeta();
                                                skull.setOwningPlayer(p);
                                                item.setItemMeta(skull);
                                                Button playerButton = new Button(item, "&a" + p.getName());
                                                playerButton.setLore("&7Grant " + p.getName() + " access to your chest.");
                                                playerButton.setCloseOnClick(true);
                                                playerButton.setAction(new xyz.leuo.gooey.action.Action() {
                                                    @Override
                                                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                                                        lockedChest.grant(p.getUniqueId());
                                                        player.sendMessage(ChatColor.GREEN + "You have granted " + ChatColor.WHITE +  p.getName() + ChatColor.GREEN + " access to this locked chest.");
                                                    }
                                                });
                                                paginatedGUI.addButton(playerButton);
                                            }
                                        }
                                        paginatedGUI.open(player);
                                    }
                                });

                                Button revoke = new Button(Material.PLAYER_HEAD, 1, "&c&lRevoke Access");
                                revoke.setLore("&7Revoke someone's access to your locked chest.");
                                revoke.setAction(new xyz.leuo.gooey.action.Action() {
                                    @Override
                                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                                        PaginatedGUI paginatedGUI = new PaginatedGUI("Revoke Access", 36);
                                        for(UUID uuid : lockedChest.getAllowed()) {
                                            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                                            if(lockedChest.canOpen(p.getUniqueId())) {
                                                ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
                                                SkullMeta skull = (SkullMeta) item.getItemMeta();
                                                skull.setOwningPlayer(p);
                                                item.setItemMeta(skull);
                                                Button playerButton = new Button(item, "&a" + p.getName());
                                                playerButton.setLore("&7Revoke " + p.getName() + "'s access to your chest.");
                                                playerButton.setCloseOnClick(true);
                                                playerButton.setAction(new xyz.leuo.gooey.action.Action() {
                                                    @Override
                                                    public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                                                        lockedChest.revoke(p.getUniqueId());
                                                        player.sendMessage(ChatColor.GREEN + "You have revoked " + ChatColor.WHITE +  p.getName() + ChatColor.GREEN + "'s access to this locked chest.");
                                                    }
                                                });
                                                paginatedGUI.addButton(playerButton);
                                            }
                                        }
                                        paginatedGUI.open(player);
                                    }
                                });

                                gui.setButton(0, privacy);
                                gui.setButton(4, unlock);
                                gui.setButton(7, grant);
                                gui.setButton(8, revoke);
                                gui.open(player);
                            } else {
                                event.setCancelled(true);
                                player.sendMessage(ChatColor.RED + "You cannot modify " + owner + "'s locked chest.");
                                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
                            }
                        } else {
                            if (lockedChest.canOpen(player)) {
                                if(!player.isSneaking()) {
                                    if (lockedChest.getOwner().equals(player.getUniqueId())) {
                                        player.sendMessage(ChatColor.GREEN + "You opened one of your locked chests.");
                                    } else {
                                        player.sendMessage(ChatColor.GREEN + "You opened one of " + lockedChest.getOwnerName() + "'s chests.");
                                    }
                                }
                            } else {
                                event.setCancelled(true);
                                player.sendMessage(ChatColor.RED + "You cannot open " + owner + "'s locked chest.");
                                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
                            }
                        }
                    } else {
                        if(item.getType().equals(Material.TRIPWIRE_HOOK) && item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Master Key")) {
                            event.setCancelled(true);
                            GUI gui = new GUI("Locked Chest Manager", 9);
                            Button lock = new Button(Material.IRON_BARS, 1, "&6Lock Chest");
                            lock.setLore("&7Click to lock this chest.");
                            lock.setCloseOnClick(true);
                            lock.setAction((player1, gui1, button, clickType) -> {
                                plugin.getLockedChestManager().add(new LockedChest(player1.getUniqueId(), UUID.randomUUID(), block.getLocation()));
                                player1.sendMessage(ChatColor.GREEN + "Successfully locked this chest.");
                                player1.playSound(player1.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);

                                for(HumanEntity human : chest.getInventory().getViewers()) {
                                    human.sendMessage(ChatColor.RED + "The chest you were viewing is now locked.");
                                    human.closeInventory();
                                }
                            });
                            gui.setButton(4, lock);
                            gui.open(player);
                        }
                    }
                }
            }
        }
    }
}
