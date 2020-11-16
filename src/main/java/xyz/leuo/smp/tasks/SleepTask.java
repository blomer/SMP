package xyz.leuo.smp.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.leuo.smp.SMP;

public class SleepTask implements Runnable {

    private SMP plugin;
    private boolean b;
    public SleepTask(SMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int i = 0;
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.isSleeping()) {
                i++;
            }
        }

        int online = Bukkit.getOnlinePlayers().size();

        if(online > 1 && i != 0 && (online / 2 <= i)) {
            if(b) {
                return;
            }

            b = true;

            World world = Bukkit.getWorlds().get(0);
            world.setTime(0);
            if(world.hasStorm()) {
                world.setStorm(false);
                Bukkit.broadcastMessage(ChatColor.WHITE.toString() + i + " out of " + online + ChatColor.GREEN + " players are sleeping, rain rain go away!");
            } else {
                Bukkit.broadcastMessage(ChatColor.WHITE.toString() + i + " out of " + online + ChatColor.GREEN + " players are sleeping, good morning!");
            }

            Bukkit.getScheduler().runTaskLater(plugin, ()-> b = false, 200);
        }
    }
}
