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
package github.mukulx.rtpzonex.listeners;

import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.utils.ItemUtils;
import github.mukulx.rtpzonex.utils.SchedulerUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZoneWandListener implements Listener {
    private final RtpZoneX plugin;
    private final Map<UUID, AtomicBoolean> activePreviewTasks = new HashMap<>();
    public ZoneWandListener(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!ItemUtils.isZoneWand(plugin, item)) {
            return;
        }
        if (!player.hasPermission("rtpzonex.zone.manage")) {
            String msg = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("no-permission");
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }
        event.setCancelled(true);
        Location clickedLocation;
        if (event.getClickedBlock() != null) {
            clickedLocation = event.getClickedBlock().getLocation();
        } else {
            clickedLocation = player.getTargetBlockExact(100) != null
                    ? player.getTargetBlockExact(100).getLocation()
                    : null;
        }

        if (clickedLocation == null) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            plugin.getZoneManager().setPos1(player, clickedLocation);

            String msg = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("pos1-set")
                            .replace("%x%", String.valueOf(clickedLocation.getBlockX()))
                            .replace("%y%", String.valueOf(clickedLocation.getBlockY()))
                            .replace("%z%", String.valueOf(clickedLocation.getBlockZ()));

            player.sendMessage(plugin.getConfigManager().colorize(msg));
            player.playSound(clickedLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
            showPositionParticles(player, clickedLocation, Particle.HAPPY_VILLAGER);
            startContinuousPreview(player);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            plugin.getZoneManager().setPos2(player, clickedLocation);

            String msg = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("pos2-set")
                            .replace("%x%", String.valueOf(clickedLocation.getBlockX()))
                            .replace("%y%", String.valueOf(clickedLocation.getBlockY()))
                            .replace("%z%", String.valueOf(clickedLocation.getBlockZ()));

            player.sendMessage(plugin.getConfigManager().colorize(msg));
            player.playSound(clickedLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            showPositionParticles(player, clickedLocation, Particle.HAPPY_VILLAGER);
            startContinuousPreview(player);
        }
    }
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        if (ItemUtils.isZoneWand(plugin, newItem)) {
            startContinuousPreview(player);
        } else if (ItemUtils.isZoneWand(plugin, oldItem)) {
            stopContinuousPreview(player);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        stopContinuousPreview(event.getPlayer());
    }
    private void startContinuousPreview(Player player) {
        stopContinuousPreview(player);
        Location pos1 = plugin.getZoneManager().getPos1(player);
        Location pos2 = plugin.getZoneManager().getPos2(player);
        if (pos1 == null || pos2 == null || !pos1.getWorld().equals(pos2.getWorld())) {
            return;
        }
        AtomicBoolean isActive = new AtomicBoolean(true);
        activePreviewTasks.put(player.getUniqueId(), isActive);
        Runnable previewTask = new Runnable() {
            @Override
            public void run() {
                if (!isActive.get() || !player.isOnline()) {
                    activePreviewTasks.remove(player.getUniqueId());
                    return;
                }
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (!ItemUtils.isZoneWand(plugin, heldItem)) {
                    stopContinuousPreview(player);
                    return;
                }
                Location currentPos1 = plugin.getZoneManager().getPos1(player);
                Location currentPos2 = plugin.getZoneManager().getPos2(player);

                if (currentPos1 != null && currentPos2 != null &&
                        currentPos1.getWorld().equals(currentPos2.getWorld())) {
                    drawZoneBoundary(player, currentPos1, currentPos2);
                }

                if (isActive.get()) {
                    SchedulerUtil.runTaskLater(plugin, this, 10L);
                }
            }
        };
        SchedulerUtil.runTask(plugin, previewTask);
    }
    private void stopContinuousPreview(Player player) {
        AtomicBoolean isActive = activePreviewTasks.remove(player.getUniqueId());
        if (isActive != null) {
            isActive.set(false);
        }
    }
    private void showPositionParticles(Player player, Location location, Particle particle) {
        Location center = location.clone().add(0.5, 1, 0.5);
        for (int i = 0; i < 20; i++) {
            double angle = (Math.PI * 2 * i) / 20;
            double x = Math.cos(angle) * 0.5;
            double z = Math.sin(angle) * 0.5;
            Location particleLoc = center.clone().add(x, 0, z);
            player.spawnParticle(Particle.HAPPY_VILLAGER, particleLoc, 1, 0, 0, 0, 0);
        }
        player.spawnParticle(Particle.FLAME, center, 15, 0.2, 0.3, 0.2, 0.02);
    }
    private void drawZoneBoundary(Player player, Location pos1, Location pos2) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        double step = 1.0;
        for (double x = minX; x <= maxX + 1; x += step) {
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), x, minY, minZ));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), x, minY, maxZ + 1));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), x, maxY + 1, minZ));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), x, maxY + 1, maxZ + 1));
        }
        for (double z = minZ; z <= maxZ + 1; z += step) {
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), minX, minY, z));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), maxX + 1, minY, z));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), minX, maxY + 1, z));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), maxX + 1, maxY + 1, z));
        }
        for (double y = minY; y <= maxY + 1; y += step) {
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), minX, y, minZ));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), maxX + 1, y, minZ));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), minX, y, maxZ + 1));
            spawnBoundaryParticle(player, new Location(pos1.getWorld(), maxX + 1, y, maxZ + 1));
        }
    }
    private void spawnBoundaryParticle(Player player, Location location) {
        player.spawnParticle(Particle.SOUL_FIRE_FLAME, location, 1, 0, 0, 0, 0);
    }
}
