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
package github.mukulx.rtpzonex.managers;

import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.utils.SchedulerUtil;
import github.mukulx.rtpzonex.zone.RtpZone;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HologramManager {

    private final RtpZoneX plugin;
    private final Map<String, List<ArmorStand>> holograms = new HashMap<>();
    private final AtomicBoolean updateTaskRunning = new AtomicBoolean(false);
    public HologramManager(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    public void startHologramUpdates() {
        int interval = plugin.getZoneManager().getHologramUpdateInterval();
        if (interval <= 0) {
            return;
        }

        updateTaskRunning.set(true);

        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                if (!updateTaskRunning.get()) {
                    return;
                }

                for (RtpZone zone : plugin.getZoneManager().getAllZones()) {
                    updateHologram(zone);
                }

                if (updateTaskRunning.get()) {
                    SchedulerUtil.runTaskLater(plugin, this, interval);
                }
            }
        };

        SchedulerUtil.runTaskLater(plugin, updateTask, 20L);
    }
    public void stopHologramUpdates() {
        updateTaskRunning.set(false);
    }
    public void createHologram(RtpZone zone) {
        Map<String, Object> holoConfig = plugin.getZoneManager().getHologramConfig(zone.getName());
        if (holoConfig == null || !(boolean)holoConfig.getOrDefault("enabled", false)) {
            return;
        }
        removeHologram(zone.getName());
        Location center = getZoneCenter(zone);
        double heightOffset = (double)holoConfig.getOrDefault("height-offset", 2.5);
        Location holoLoc = center.clone().add(0, heightOffset, 0);
        @SuppressWarnings("unchecked")
        List<String> lines = (List<String>)holoConfig.getOrDefault("lines", new ArrayList<>());
        SchedulerUtil.runTaskAtLocation(plugin, holoLoc, () -> {
            List<ArmorStand> stands = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                Location lineLoc = holoLoc.clone().subtract(0, i * 0.25, 0);
                ArmorStand stand = (ArmorStand) lineLoc.getWorld().spawnEntity(lineLoc, EntityType.ARMOR_STAND);
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setInvulnerable(true);
                stand.setMarker(true);
                stand.setCustomNameVisible(true);
                stand.setPersistent(false);
                String line = formatHologramLine(lines.get(i), zone);
                stand.customName(plugin.getConfigManager().colorize(line));
                stands.add(stand);
            }
            holograms.put(zone.getName(), stands);
        });
    }
    public void updateHologram(RtpZone zone) {
        List<ArmorStand> stands = holograms.get(zone.getName());
        if (stands == null || stands.isEmpty()) {
            createHologram(zone);
            return;
        }
        Map<String, Object> holoConfig = plugin.getZoneManager().getHologramConfig(zone.getName());
        if (holoConfig == null) return;
        @SuppressWarnings("unchecked")
        List<String> lines = (List<String>)holoConfig.getOrDefault("lines", new ArrayList<>());
        if (!stands.isEmpty() && stands.get(0).isValid()) {
            Location standLoc = stands.get(0).getLocation();
            SchedulerUtil.runTaskAtLocation(plugin, standLoc, () -> {
                for (int i = 0; i < Math.min(lines.size(), stands.size()); i++) {
                    ArmorStand stand = stands.get(i);
                    if (stand.isValid()) {
                        String line = formatHologramLine(lines.get(i), zone);
                        stand.customName(plugin.getConfigManager().colorize(line));
                    }
                }
            });
        }
    }
    public void removeHologram(String zoneName) {
        List<ArmorStand> stands = holograms.remove(zoneName);
        if (stands != null && !stands.isEmpty()) {
            if (stands.get(0).isValid()) {
                Location standLoc = stands.get(0).getLocation();
                SchedulerUtil.runTaskAtLocation(plugin, standLoc, () -> {
                    for (ArmorStand stand : stands) {
                        if (stand.isValid()) {
                            stand.remove();
                        }
                    }
                });
            }
        }
    }
    public void removeAllHolograms() {
        for (String zoneName : new ArrayList<>(holograms.keySet())) {
            removeHologram(zoneName);
        }
    }
    private Location getZoneCenter(RtpZone zone) {
        Location pos1 = zone.getPos1();
        Location pos2 = zone.getPos2();
        double centerX = (pos1.getX() + pos2.getX()) / 2;
        double centerY = Math.max(pos1.getY(), pos2.getY());
        double centerZ = (pos1.getZ() + pos2.getZ()) / 2;
        return new Location(pos1.getWorld(), centerX, centerY, centerZ);
    }
    private String formatHologramLine(String line, RtpZone zone) {
        String status;
        if (zone.isTeleportInProgress()) {
            status = "&aTeleporting...";
        } else if (zone.isWaitingForPlayers()) {
            status = "&eWaiting (" + zone.getWaitTimeRemaining() + "s)";
        } else {
            status = "&7Idle";
        }
        return line
        .replace("%zone%", zone.getName())
        .replace("%players%", String.valueOf(zone.getPlayerCount()))
        .replace("%max%", String.valueOf(zone.getConfig().getMaxPlayers()))
        .replace("%status%", status)
        .replace("%countdown%", String.valueOf(zone.getConfig().getTeleportCountdown()))
        .replace("%world%", zone.getConfig().getTargetWorld());
    }
}
