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
import github.mukulx.rtpzonex.gui.ZoneConfigGUI;
import github.mukulx.rtpzonex.zone.RtpZone;
import github.mukulx.rtpzonex.zone.ZoneConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatInputListener implements Listener {
    private final RtpZoneX plugin;
    private final Map<UUID, InputRequest> pendingInputs = new HashMap<>();
    public enum InputType {
        MIN_DISTANCE,
        MAX_DISTANCE,
        TELEPORT_COUNTDOWN,
        WAIT_TIME,
        COOLDOWN,
        MIN_PLAYERS,
        MAX_PLAYERS,
        PARTICLE_COUNT,
        PREFIX
    }
    public record InputRequest(RtpZone zone, InputType type) {}
    public ChatInputListener(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    public void requestInput(Player player, RtpZone zone, InputType type) {
        pendingInputs.put(player.getUniqueId(), new InputRequest(zone, type));
        player.closeInventory();
        String message = switch (type) {
            case MIN_DISTANCE -> "&eEnter the minimum distance (number):";
            case MAX_DISTANCE -> "&eEnter the maximum distance (number):";
            case TELEPORT_COUNTDOWN -> "&eEnter the teleport countdown in seconds (1-60):";
            case WAIT_TIME -> "&eEnter the wait time in seconds (1-120):";
            case COOLDOWN -> "&eEnter the cooldown in seconds (0-3600):";
            case MIN_PLAYERS -> "&eEnter the minimum players (1-50):";
            case MAX_PLAYERS -> "&eEnter the maximum players (1-50):";
            case PARTICLE_COUNT -> "&eEnter the particle count (1-200):";
            case PREFIX -> "&eEnter the new prefix (use & for colors):";
        };
        player.sendMessage(plugin.getConfigManager().colorize(plugin.getConfigManager().getPrefix() + message));
        player.sendMessage(plugin.getConfigManager().colorize("&7Type &ccancel &7to cancel."));
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        InputRequest request = pendingInputs.get(player.getUniqueId());

        if (request == null) {
            return;
        }

        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (input.equalsIgnoreCase("cancel")) {
            pendingInputs.remove(player.getUniqueId());
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTask(plugin, () -> {
                player.sendMessage(plugin.getConfigManager().colorize(plugin.getConfigManager().getPrefix() + "&cCancelled."));
                new ZoneConfigGUI(plugin, request.zone(), player).openMainMenu();
            });
            return;
        }

        if (request.type() == InputType.PREFIX) {
            pendingInputs.remove(player.getUniqueId());
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTask(plugin, () -> {
                plugin.getConfigManager().setPrefix(input);
                player.sendMessage(plugin.getConfigManager().colorize(plugin.getConfigManager().getPrefix() + "&aPrefix updated!"));
                new ZoneConfigGUI(plugin, request.zone(), player).openMainMenu();
            });
            return;
        }

        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTask(plugin, () -> {
                player.sendMessage(plugin.getConfigManager().colorize(plugin.getConfigManager().getPrefix() + "&cInvalid number! Try again or type 'cancel'."));
            });
            return;
        }

        pendingInputs.remove(player.getUniqueId());
        ZoneConfig config = request.zone().getConfig();

        github.mukulx.rtpzonex.utils.SchedulerUtil.runTask(plugin, () -> {
            boolean valid = true;
            String feedback = "";

            switch (request.type()) {
                case MIN_DISTANCE -> {
                    if (value >= 0 && value < config.getMaxDistance()) {
                        config.setMinDistance(value);
                        feedback = "Min distance set to " + value;
                    } else {
                        valid = false;
                        feedback = "Value must be between 0 and " + (config.getMaxDistance() - 1);
                    }
                }
                case MAX_DISTANCE -> {
                    if (value > config.getMinDistance() && value <= 100000) {
                        config.setMaxDistance(value);
                        feedback = "Max distance set to " + value;
                    } else {
                        valid = false;
                        feedback = "Value must be greater than " + config.getMinDistance();
                    }
                }
                case TELEPORT_COUNTDOWN -> {
                    if (value >= 1 && value <= 60) {
                        config.setTeleportCountdown(value);
                        feedback = "Teleport countdown set to " + value + "s";
                    } else {
                        valid = false;
                        feedback = "Value must be between 1 and 60";
                    }
                }
                case WAIT_TIME -> {
                    if (value >= 1 && value <= 120) {
                        config.setWaitTime(value);
                        feedback = "Wait time set to " + value + "s";
                    } else {
                        valid = false;
                        feedback = "Value must be between 1 and 120";
                    }
                }
                case COOLDOWN -> {
                    if (value >= 0 && value <= 3600) {
                        config.setCooldown(value);
                        feedback = "Cooldown set to " + value + "s";
                    } else {
                        valid = false;
                        feedback = "Value must be between 0 and 3600";
                    }
                }
                case MIN_PLAYERS -> {
                    if (value >= 1 && value <= config.getMaxPlayers()) {
                        config.setMinPlayers(value);
                        feedback = "Min players set to " + value;
                    } else {
                        valid = false;
                        feedback = "Value must be between 1 and " + config.getMaxPlayers();
                    }
                }
                case MAX_PLAYERS -> {
                    if (value >= config.getMinPlayers() && value <= 50) {
                        config.setMaxPlayers(value);
                        feedback = "Max players set to " + value;
                    } else {
                        valid = false;
                        feedback = "Value must be between " + config.getMinPlayers() + " and 50";
                    }
                }
                case PARTICLE_COUNT -> {
                    if (value >= 1 && value <= 200) {
                        config.setParticleCount(value);
                        feedback = "Particle count set to " + value;
                    } else {
                        valid = false;
                        feedback = "Value must be between 1 and 200";
                    }
                }
                default -> {
                }
            }

            if (valid) {
                plugin.getZoneManager().saveZones();
                player.sendMessage(plugin.getConfigManager().colorize(plugin.getConfigManager().getPrefix() + "&a" + feedback));
            } else {
                player.sendMessage(plugin.getConfigManager().colorize(plugin.getConfigManager().getPrefix() + "&c" + feedback));
            }

            new ZoneConfigGUI(plugin, request.zone(), player).openMainMenu();
        });
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pendingInputs.remove(event.getPlayer().getUniqueId());
    }
    public boolean hasPendingInput(UUID playerId) {
        return pendingInputs.containsKey(playerId);
    }
    public void cancelInput(UUID playerId) {
        pendingInputs.remove(playerId);
    }
}
