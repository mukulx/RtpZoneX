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
package github.mukulx.rtpzonex.teleport;
import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.utils.SchedulerUtil;
import github.mukulx.rtpzonex.zone.RtpZone;
import github.mukulx.rtpzonex.zone.ZoneConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
public class TeleportManager {
    private final RtpZoneX plugin;
    private final SafeLocationFinder locationFinder;
    private final Map<String, AtomicBoolean> activeTeleports;
    private final Map<String, AtomicBoolean> waitingTasks;
    private final Map<String, AtomicBoolean> zoneParticleTasks;
    private final Map<UUID, String> playerTeleportZone;
    public TeleportManager(RtpZoneX plugin) {
        this.plugin = plugin;
        this.locationFinder = new SafeLocationFinder(plugin);
        this.activeTeleports = new HashMap<>();
        this.waitingTasks = new HashMap<>();
        this.zoneParticleTasks = new HashMap<>();
        this.playerTeleportZone = new HashMap<>();
    }
    public void playerEnteredZone(RtpZone zone, Player player) {
        ZoneConfig config = zone.getConfig();
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), config.getCooldown())) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), config.getCooldown());
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("teleport-cooldown")
            .replace("%time%", String.valueOf(remaining));
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }
        if (zone.getPlayerCount() >= config.getMaxPlayers()) {
            player.sendMessage(plugin.getConfigManager().colorize(
            plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("zone-full")));
            return;
        }
        zone.addPlayer(player.getUniqueId());
        playerTeleportZone.put(player.getUniqueId(), zone.getName());
        String msg = plugin.getConfigManager().getPrefix() +
        plugin.getConfigManager().getMessage("zone-entered")
        .replace("%zone%", zone.getName());
        player.sendMessage(plugin.getConfigManager().colorize(msg));
        String joinMsg = plugin.getConfigManager().getMessage("player-joined-group")
        .replace("%player%", player.getName());
        notifyPlayersInZone(zone, joinMsg);
        startZoneParticles(zone);
        if (zone.isWaitingForPlayers() || zone.isTeleportInProgress()) {
            if (config.isDarknessEnabled()) {
                int duration = (config.getWaitTime() + config.getTeleportCountdown() + 5) * 20;
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0, false, false));
            }
            return;
        }
        if (config.isWaitForPlayers()) {
            startWaitingPhase(zone);
        } else {
            startImmediateTeleport(zone);
        }
    }
    private void startZoneParticles(RtpZone zone) {
        if (!zone.getConfig().isZoneParticlesEnabled()) return;
        if (zoneParticleTasks.containsKey(zone.getName())) return;
        AtomicBoolean isActive = new AtomicBoolean(true);
        zoneParticleTasks.put(zone.getName(), isActive);
        Runnable particleTask = new Runnable() {
            @Override
            public void run() {
                if (!isActive.get() || zone.getPlayerCount() == 0 || !zone.getConfig().isZoneParticlesEnabled()) {
                    zoneParticleTasks.remove(zone.getName());
                    return;
                }
                Location pos1 = zone.getPos1();
                Location pos2 = zone.getPos2();
                World world = zone.getWorld();
                Particle particle = zone.getConfig().getParticleType();
                double minX = Math.min(pos1.getX(), pos2.getX());
                double maxX = Math.max(pos1.getX(), pos2.getX());
                double minY = Math.min(pos1.getY(), pos2.getY());
                double maxY = Math.max(pos1.getY(), pos2.getY());
                double minZ = Math.min(pos1.getZ(), pos2.getZ());
                double maxZ = Math.max(pos1.getZ(), pos2.getZ());
                try {
                    for (double x = minX; x <= maxX; x += 1.0) {
                        spawnParticleSafe(world, particle, new Location(world, x, minY, minZ), 1);
                        spawnParticleSafe(world, particle, new Location(world, x, minY, maxZ), 1);
                        spawnParticleSafe(world, particle, new Location(world, x, maxY, minZ), 1);
                        spawnParticleSafe(world, particle, new Location(world, x, maxY, maxZ), 1);
                    }
                    for (double z = minZ; z <= maxZ; z += 1.0) {
                        spawnParticleSafe(world, particle, new Location(world, minX, minY, z), 1);
                        spawnParticleSafe(world, particle, new Location(world, maxX, minY, z), 1);
                        spawnParticleSafe(world, particle, new Location(world, minX, maxY, z), 1);
                        spawnParticleSafe(world, particle, new Location(world, maxX, maxY, z), 1);
                    }
                    for (double y = minY; y <= maxY; y += 1.0) {
                        spawnParticleSafe(world, particle, new Location(world, minX, y, minZ), 1);
                        spawnParticleSafe(world, particle, new Location(world, maxX, y, minZ), 1);
                        spawnParticleSafe(world, particle, new Location(world, minX, y, maxZ), 1);
                        spawnParticleSafe(world, particle, new Location(world, maxX, y, maxZ), 1);
                    }
                } catch (Exception ignored) {}
                if (isActive.get()) {
                    SchedulerUtil.runTaskLater(plugin, this, 10L);
                }
            }
        };
        SchedulerUtil.runTask(plugin, particleTask);
    }
    private void spawnParticleSafe(World world, Particle particle, Location location, int count) {
        try {
            switch (particle) {
                case DUST:
                world.spawnParticle(particle, location, count, 0, 0, 0, 0,
                new Particle.DustOptions(org.bukkit.Color.PURPLE, 1.0f));
                break;
                case DUST_COLOR_TRANSITION:
                world.spawnParticle(particle, location, count, 0, 0, 0, 0,
                new Particle.DustTransition(org.bukkit.Color.PURPLE, org.bukkit.Color.BLUE, 1.0f));
                break;
                case BLOCK:
                case FALLING_DUST:
                case ITEM:
                world.spawnParticle(Particle.FLAME, location, count, 0, 0, 0, 0);
                break;
                default:
                world.spawnParticle(particle, location, count, 0, 0, 0, 0);
                break;
            }
        } catch (Exception e) {
            try {
                world.spawnParticle(Particle.FLAME, location, count, 0, 0, 0, 0);
            } catch (Exception ignored) {}
        }
    }
    private void startImmediateTeleport(RtpZone zone) {
        ZoneConfig config = zone.getConfig();
        zone.setTeleportInProgress(true);
        if (config.isDarknessEnabled()) {
            int duration = (config.getTeleportCountdown() + 2) * 20;
            for (UUID playerId : zone.getPlayersInZone()) {
                Player p = Bukkit.getPlayer(playerId);
                if (p != null && p.isOnline()) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0, false, false));
                }
            }
        }
        World targetWorld = Bukkit.getWorld(config.getTargetWorld());
        if (targetWorld == null) {
            targetWorld = zone.getWorld();
        }
        final World finalWorld = targetWorld;
        locationFinder.findSafeLocation(finalWorld, config).thenAccept(location -> {
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTask(plugin, () -> {
                if (location == null) {
                    cancelZoneTeleport(zone, plugin.getConfigManager().getMessage("no-safe-location"));
                    return;
                }
                zone.setPendingTeleportLocation(location);
                locationFinder.markLocationUsed(location);
                startGroupTeleport(zone);
            });
        });
    }
    private void startWaitingPhase(RtpZone zone) {
        ZoneConfig config = zone.getConfig();
        zone.setWaitingForPlayers(true);
        zone.setWaitTimeRemaining(config.getWaitTime());
        if (config.isDarknessEnabled()) {
            int duration = (config.getWaitTime() + config.getTeleportCountdown() + 5) * 20;
            for (UUID playerId : zone.getPlayersInZone()) {
                Player p = Bukkit.getPlayer(playerId);
                if (p != null && p.isOnline()) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0, false, false));
                }
            }
        }
        World targetWorld = Bukkit.getWorld(config.getTargetWorld());
        if (targetWorld == null) {
            targetWorld = zone.getWorld();
        }
        final World finalWorld = targetWorld;
        locationFinder.findSafeLocation(finalWorld, config).thenAccept(location -> {
            SchedulerUtil.runTask(plugin, () -> {
                if (location == null) {
                    cancelZoneTeleport(zone, plugin.getConfigManager().getMessage("no-safe-location"));
                    return;
                }
                zone.setPendingTeleportLocation(location);
                locationFinder.markLocationUsed(location);
            });
        });
        AtomicBoolean isActive = new AtomicBoolean(true);
        waitingTasks.put(zone.getName(), isActive);
        Runnable waitTask = new Runnable() {
            @Override
            public void run() {
                if (!isActive.get() || !zone.isWaitingForPlayers()) {
                    waitingTasks.remove(zone.getName());
                    return;
                }
                int remaining = zone.getWaitTimeRemaining();
                if (remaining <= 0) {
                    zone.setWaitingForPlayers(false);
                    waitingTasks.remove(zone.getName());
                    if (zone.getPlayerCount() < config.getMinPlayers()) {
                        cancelZoneTeleport(zone, plugin.getConfigManager().getMessage("not-enough-players")
                        .replace("%min%", String.valueOf(config.getMinPlayers()))
                        .replace("%current%", String.valueOf(zone.getPlayerCount())));
                        return;
                    }
                    startGroupTeleport(zone);
                    return;
                }
                showWaitingEffects(zone, remaining);
                zone.decrementWaitTime();
                if (isActive.get()) {
                    SchedulerUtil.runTaskLater(plugin, this, 20L);
                }
            }
        };
        SchedulerUtil.runTask(plugin, waitTask);
    }
    private void showWaitingEffects(RtpZone zone, int waitTime) {
        ZoneConfig config = zone.getConfig();
        int playerCount = zone.getPlayerCount();
        int maxPlayers = config.getMaxPlayers();
        for (UUID playerId : zone.getPlayersInZone()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline()) continue;
            if (config.isTitlesEnabled()) {
                String titleText = plugin.getConfigManager().getMessagesConfig()
                .getString("titles.waiting.title", "&aWaiting for players...");
                String subtitleText = plugin.getConfigManager().getMessagesConfig()
                .getString("titles.waiting.subtitle", "&7%count% players ready. Teleporting in &#FF0000%wait%s")
                .replace("%count%", String.valueOf(playerCount))
                .replace("%max%", String.valueOf(maxPlayers))
                .replace("%wait%", String.valueOf(waitTime));
                Title.Times times = Title.Times.times(
                Duration.ofMillis(0),
                Duration.ofMillis(1100),
                Duration.ofMillis(0)
                );
                Title title = Title.title(
                plugin.getConfigManager().colorize(titleText),
                plugin.getConfigManager().colorize(subtitleText),
                times
                );
                player.showTitle(title);
            }
            if (plugin.getConfigManager().isActionBarEnabled()) {
                String actionBar = plugin.getConfigManager().getMessagesConfig()
                .getString("actionbar.waiting", "&6Ã¢ÂÂ³ &7Waiting for players... &e%count%/%max%")
                .replace("%count%", String.valueOf(playerCount))
                .replace("%max%", String.valueOf(maxPlayers))
                .replace("%wait%", String.valueOf(waitTime));
                player.sendActionBar(plugin.getConfigManager().colorize(actionBar));
            }
        }
    }
    public void startGroupTeleport(RtpZone zone) {
        if (zone.isTeleportInProgress() && !zone.isWaitingForPlayers()) {
        } else {
            zone.setTeleportInProgress(true);
        }
        zone.setWaitingForPlayers(false);
        Set<UUID> playerIds = zone.getPlayersInZone();
        if (playerIds.isEmpty()) {
            zone.resetTeleportState();
            return;
        }
        List<Player> players = playerIds.stream()
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .filter(Player::isOnline)
        .collect(Collectors.toList());
        if (players.isEmpty()) {
            zone.resetTeleportState();
            return;
        }
        ZoneConfig config = zone.getConfig();
        int countdown = config.getTeleportCountdown();
        AtomicInteger timeLeft = new AtomicInteger(countdown);
        AtomicBoolean isActive = new AtomicBoolean(true);
        activeTeleports.put(zone.getName(), isActive);
        Runnable teleportTask = new Runnable() {
            @Override
            public void run() {
                if (!isActive.get()) {
                    activeTeleports.remove(zone.getName());
                    return;
                }
                List<Player> currentPlayers = zone.getPlayersInZone().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .collect(Collectors.toList());
                if (currentPlayers.isEmpty()) {
                    zone.resetTeleportState();
                    activeTeleports.remove(zone.getName());
                    return;
                }
                if (timeLeft.get() <= 0) {
                    executeTeleport(zone, currentPlayers);
                    return;
                }
                showCountdownEffects(zone, currentPlayers, timeLeft.get());
                timeLeft.decrementAndGet();
                if (isActive.get()) {
                    SchedulerUtil.runTaskLater(plugin, this, 20L);
                }
            }
        };
        SchedulerUtil.runTask(plugin, teleportTask);
    }
    private void showCountdownEffects(RtpZone zone, List<Player> players, int timeLeft) {
        ZoneConfig config = zone.getConfig();
        for (Player player : players) {
            String otherPlayerNames = players.stream()
            .filter(p -> !p.equals(player))
            .map(Player::getName)
            .collect(Collectors.joining(", "));
            if (config.isTitlesEnabled()) {
                String titleText = plugin.getConfigManager().getMessagesConfig()
                .getString("titles.countdown.title", "&aTeleporting...");
                String subtitleText;
                if (otherPlayerNames.isEmpty()) {
                    subtitleText = plugin.getConfigManager().getMessagesConfig()
                    .getString("titles.countdown.subtitle-solo", "&7Only you will teleport in &#FF0000%countdown%s")
                    .replace("%countdown%", String.valueOf(timeLeft));
                } else {
                    subtitleText = plugin.getConfigManager().getMessagesConfig()
                    .getString("titles.countdown.subtitle", "&7You and &e%players% &7will teleport in &#FF0000%countdown%s")
                    .replace("%countdown%", String.valueOf(timeLeft))
                    .replace("%players%", otherPlayerNames);
                }
                Title.Times times = Title.Times.times(
                Duration.ofMillis(0),
                Duration.ofMillis(1100),
                Duration.ofMillis(0)
                );
                Title title = Title.title(
                plugin.getConfigManager().colorize(titleText),
                plugin.getConfigManager().colorize(subtitleText),
                times
                );
                player.showTitle(title);
            }
            if (plugin.getConfigManager().isActionBarEnabled()) {
                String actionBar = plugin.getConfigManager().getMessagesConfig()
                .getString("actionbar.countdown", "&aÃ¢ÂÂ± &fTeleporting in &a%countdown% &fseconds...")
                .replace("%countdown%", String.valueOf(timeLeft))
                .replace("%players%", otherPlayerNames.isEmpty() ? "no one else" : otherPlayerNames);
                player.sendActionBar(plugin.getConfigManager().colorize(actionBar));
            }
        }
    }
    private void executeTeleport(RtpZone zone, List<Player> players) {
        ZoneConfig config = zone.getConfig();
        Location location = zone.getPendingTeleportLocation();
        if (location == null) {
            cancelZoneTeleport(zone, plugin.getConfigManager().getMessage("no-safe-location"));
            return;
        }
        for (Player player : players) {
            SchedulerUtil.runTaskAtLocation(plugin, location, () -> {
                teleportPlayer(player, location, config);
                playerTeleportZone.remove(player.getUniqueId());
            });
        }
        AtomicBoolean particleTask = zoneParticleTasks.remove(zone.getName());
        if (particleTask != null) {
            particleTask.set(false);
        }
        zone.clearPlayers();
        zone.resetTeleportState();
        activeTeleports.remove(zone.getName());
        String playerNames = players.stream().map(Player::getName).collect(Collectors.joining(", "));
        plugin.getLogger().info("Teleported group to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ": " + playerNames);
    }
    private void teleportPlayer(Player player, Location location, ZoneConfig config) {
        if (SchedulerUtil.isFolia()) {
            player.teleportAsync(location).thenAccept(success -> {
                if (!success) {
                    plugin.getLogger().warning("Failed to teleport player " + player.getName());
                    return;
                }
                applyTeleportEffects(player, location, config);
            });
        } else {
            player.teleport(location);
            applyTeleportEffects(player, location, config);
        }
    }
    private void applyTeleportEffects(Player player, Location location, ZoneConfig config) {
        player.removePotionEffect(PotionEffectType.DARKNESS);
        if (config.isSoundEnabled()) {
            player.playSound(location, config.getTeleportSound(), config.getSoundVolume(), config.getSoundPitch());
        }
        if (config.isParticlesEnabled()) {
            try {
                Particle particle = config.getParticleType();
                int count = config.getParticleCount();
                spawnParticleSafe(player.getWorld(), particle, location.clone().add(0, 1, 0), count);
                spawnParticleSafe(player.getWorld(), particle, location, count / 2);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not spawn particle: " + e.getMessage());
            }
        }
        if (config.isTitlesEnabled()) {
            Title.Times times = Title.Times.times(
            Duration.ofMillis(200),
            Duration.ofMillis(1500),
            Duration.ofMillis(200)
            );
            String titleText = plugin.getConfigManager().getMessagesConfig()
            .getString("titles.teleported.title", "&a&lTeleported!");
            String subtitleText;
            if (config.isShowCoordinates()) {
                subtitleText = plugin.getConfigManager().getMessagesConfig()
                .getString("titles.teleported-coords.subtitle", "&7Location: &e%x%, %y%, %z%")
                .replace("%x%", String.valueOf(location.getBlockX()))
                .replace("%y%", String.valueOf(location.getBlockY()))
                .replace("%z%", String.valueOf(location.getBlockZ()));
            } else {
                subtitleText = plugin.getConfigManager().getMessagesConfig()
                .getString("titles.teleported.subtitle", "&7You arrived at a random location");
            }
            Title title = Title.title(
            plugin.getConfigManager().colorize(titleText),
            plugin.getConfigManager().colorize(subtitleText),
            times
            );
            player.showTitle(title);
        }
        if (plugin.getConfigManager().isActionBarEnabled()) {
            String actionBar = plugin.getConfigManager().getMessagesConfig()
            .getString("actionbar.teleported", "&aÃ¢Å“â€œ &7Successfully teleported!");
            if (config.isShowCoordinates()) {
                actionBar = actionBar + " &7(&e" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "&7)";
            }
            player.sendActionBar(plugin.getConfigManager().colorize(actionBar));
        }
        if (config.isShowCoordinates()) {
            String coordsMsg = plugin.getConfigManager().getMessage("teleported-to")
            .replace("%x%", String.valueOf(location.getBlockX()))
            .replace("%y%", String.valueOf(location.getBlockY()))
            .replace("%z%", String.valueOf(location.getBlockZ()))
            .replace("%world%", location.getWorld().getName());
            player.sendMessage(plugin.getConfigManager().colorize(
            plugin.getConfigManager().getPrefix() + coordsMsg));
        }
        plugin.getCooldownManager().setCooldown(player.getUniqueId());
        plugin.getCooldownManager().incrementTeleportCount(player.getUniqueId());
    }
    private void cancelZoneTeleport(RtpZone zone, String reason) {
        for (UUID playerId : zone.getPlayersInZone()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.removePotionEffect(PotionEffectType.DARKNESS);
                player.sendMessage(plugin.getConfigManager().colorize(
                plugin.getConfigManager().getPrefix() + "&c" + reason));
            }
            playerTeleportZone.remove(playerId);
        }
        AtomicBoolean particleTask = zoneParticleTasks.remove(zone.getName());
        if (particleTask != null) {
            particleTask.set(false);
        }
        zone.clearPlayers();
        zone.resetTeleportState();
        activeTeleports.remove(zone.getName());
        waitingTasks.remove(zone.getName());
    }
    private void notifyPlayersInZone(RtpZone zone, String message) {
        for (UUID playerId : zone.getPlayersInZone()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage(plugin.getConfigManager().colorize(
                plugin.getConfigManager().getPrefix() + message));
            }
        }
    }
    public void playerLeftZone(RtpZone zone, Player player) {
        boolean wasInZone = zone.hasPlayer(player.getUniqueId());
        zone.removePlayer(player.getUniqueId());
        playerTeleportZone.remove(player.getUniqueId());
        if (wasInZone) {
            player.removePotionEffect(PotionEffectType.DARKNESS);
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("zone-left")
            .replace("%zone%", zone.getName());
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            String leftMsg = plugin.getConfigManager().getMessage("player-left-group")
            .replace("%player%", player.getName());
            notifyPlayersInZone(zone, leftMsg);
            if (zone.getPlayerCount() == 0) {
                cancelZoneTeleport(zone, plugin.getConfigManager().getMessage("all-players-left"));
            }
        }
    }
    public void cancelAllTeleports() {
        for (AtomicBoolean task : activeTeleports.values()) {
            task.set(false);
        }
        for (AtomicBoolean task : waitingTasks.values()) {
            task.set(false);
        }
        for (AtomicBoolean task : zoneParticleTasks.values()) {
            task.set(false);
        }
        activeTeleports.clear();
        waitingTasks.clear();
        zoneParticleTasks.clear();
        playerTeleportZone.clear();
        for (RtpZone zone : plugin.getZoneManager().getAllZones()) {
            zone.resetTeleportState();
        }
    }
    public boolean isPlayerTeleporting(UUID playerId) {
        return playerTeleportZone.containsKey(playerId);
    }
    public String getPlayerTeleportZone(UUID playerId) {
        return playerTeleportZone.get(playerId);
    }
    public SafeLocationFinder getLocationFinder() {
        return locationFinder;
    }
}
