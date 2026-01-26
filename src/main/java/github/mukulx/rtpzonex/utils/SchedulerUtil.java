/*
 * RtpZoneX - https://github.com/mukulx/RtpZoneX
 * Copyright (C) 2025 Mukulx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package github.mukulx.rtpzonex.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class SchedulerUtil {

    private static boolean isFolia = false;
    private static final List<ScheduledTask> foliaTasks = new ArrayList<>();

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
    }

    public static boolean isFolia() {
        return isFolia;
    }

    public static void runTask(Plugin plugin, Runnable task) {
        if (isFolia) {
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runTaskLater(Plugin plugin, Runnable task, long delay) {
        if (isFolia) {
            ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, st -> task.run(), delay);
            synchronized (foliaTasks) {
                foliaTasks.add(scheduledTask);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    public static void runTaskTimer(Plugin plugin, Runnable task, long delay, long period) {
        if (isFolia) {
            ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, st -> task.run(), delay, period);
            synchronized (foliaTasks) {
                foliaTasks.add(scheduledTask);
            }
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    public static void runTaskAtLocation(Plugin plugin, Location location, Runnable task) {
        if (isFolia) {
            Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runTaskTimerAtLocation(Plugin plugin, Location location, Runnable task, long delay, long period) {
        if (isFolia) {
            ScheduledTask scheduledTask = Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, st -> task.run(), delay, period);
            synchronized (foliaTasks) {
                foliaTasks.add(scheduledTask);
            }
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    public static void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
        if (isFolia) {
            entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runTaskAsync(Plugin plugin, Runnable task) {
        if (isFolia) {
            Bukkit.getAsyncScheduler().runNow(plugin, st -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    public static void cancelAllTasks(Plugin plugin) {
        if (isFolia) {
            synchronized (foliaTasks) {
                for (ScheduledTask task : foliaTasks) {
                    try {
                        task.cancel();
                    } catch (Exception ignored) {
                    }
                }
                foliaTasks.clear();
            }
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }
}
