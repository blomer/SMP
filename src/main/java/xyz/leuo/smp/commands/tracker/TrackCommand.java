package xyz.leuo.smp.commands.tracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.leuo.smp.SMP;
import xyz.leuo.smp.profiles.Profile;

public class TrackCommand implements CommandExecutor {

    private SMP plugin;
    public TrackCommand(SMP plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("track").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length > 2) {
                World world;
                if(args.length > 3) {
                    world = Bukkit.getWorld(args[3]);
                } else {
                    world = player.getWorld();
                }

                if(world != null && world == player.getWorld()) {
                    try {
                        double x = Double.parseDouble(args[0]);
                        double y = Double.parseDouble(args[1]);
                        double z = Double.parseDouble(args[2]);
                        Location location = new Location(world, x, y, z);
                        Profile profile = plugin.getProfileManager().find(player);
                        profile.track(world.getName(), location);
                        player.sendMessage(ChatColor.GREEN + "Updated tracker.");
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "You did not specify a valid location.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The world you specified does not exist or you are not in that world at the moment.");
                }
            }
        }

        return true;
    }
}
