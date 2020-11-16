package xyz.leuo.smp.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.leuo.smp.SMP;

public class CoordinatesCommand implements CommandExecutor {

    private SMP plugin;
    public CoordinatesCommand(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("coordinates").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    player.sendMessage(ChatColor.GREEN + "You shared your coordinates with " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
                    TextComponent text = new TextComponent(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " shared their coordinates with you: " + ChatColor.WHITE + "X: " + (int) Math.round(location.getX()) + ", Y: " + (int) Math.round(location.getY()) + ", Z: " + (int) Math.round(location.getZ()) +
                            ChatColor.GRAY + (player.getWorld().equals(target.getWorld()) ? " (" + (int) Math.round(location.distance(target.getLocation())) + " block(s) away from you)" + "\n" + ChatColor.GRAY + ChatColor.ITALIC + "Click to track!" : ""));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Click to track " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ".")));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/track " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getWorld().getName()));
                    target.spigot().sendMessage(text);
                } else {
                    player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
                }
            } else {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    TextComponent text = new TextComponent(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " shared their coordinates: " + ChatColor.WHITE + "X: " + (int) Math.round(location.getX()) + ", Y: " + (int) Math.round(location.getY()) + ", Z: " + (int) Math.round(location.getZ()) +
                            ChatColor.GRAY + (player.getWorld().equals(p.getWorld()) ? " (" + (int) Math.round(location.distance(p.getLocation())) + " block(s) away from you)" + "\n" + ChatColor.GRAY + ChatColor.ITALIC + "Click to track!" : ""));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Click to track " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ".")));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/track " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getWorld().getName()));
                    p.spigot().sendMessage(text);
                }
            }
        }

        return true;
    }
}
