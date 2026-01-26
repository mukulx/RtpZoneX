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
package github.mukulx.rtpzonex.commands;

import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.gui.ZoneListGUI;
import github.mukulx.rtpzonex.zone.RtpZone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RtpZoneAdminCommand implements CommandExecutor, TabCompleter {
    private final RtpZoneX plugin;
    public RtpZoneAdminCommand(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "info":
                handleInfo(sender);
                break;
            case "zones":
                handleZones(sender);
                break;
            case "gui":
                handleGUI(sender);
                break;
            case "debug":
                handleDebug(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().colorize("&6&l=== RtpZoneX Admin Help ==="));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzoneadmin help &7- Show this help"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzoneadmin gui &7- Open zone management GUI"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzoneadmin reload &7- Reload configuration"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzoneadmin info &7- Show plugin info"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzoneadmin zones &7- List all zones"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzoneadmin debug &7- Toggle debug mode"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone &7- Open zone list GUI"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone wand &7- Get zone wand"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone create <name> &7- Create a zone"));
        sender.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone config <name> &7- Configure a zone"));
    }
    private void handleGUI(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-only")));
            return;
        }

        if (!player.hasPermission("rtpzonex.zone.manage")) {
            String msg = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("no-permission");
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }

        new ZoneListGUI(plugin, player).open();
    }
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("rtpzonex.reload")) {
            String msg = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("no-permission");
            sender.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }

        plugin.reload();

        String msg = plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("config-reloaded");
        sender.sendMessage(plugin.getConfigManager().colorize(msg));
    }
    private void handleInfo(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().colorize("&6&l=== RtpZoneX Info ==="));
        sender.sendMessage(plugin.getConfigManager().colorize("&7Version: &e" + plugin.getDescription().getVersion()));
        sender.sendMessage(plugin.getConfigManager().colorize("&7Author: &e" + plugin.getDescription().getAuthors().get(0)));
        sender.sendMessage(plugin.getConfigManager().colorize("&7Zones: &e" + plugin.getZoneManager().getZoneCount()));
        sender.sendMessage(plugin.getConfigManager().colorize("&7Default Countdown: &e" +
                plugin.getConfigManager().getTeleportCountdown() + "s"));
        sender.sendMessage(plugin.getConfigManager().colorize("&7Cooldown: &e" +
                plugin.getConfigManager().getCooldown() + "s"));
    }
    private void handleZones(CommandSender sender) {
        var zones = plugin.getZoneManager().getAllZones();

        if (zones.isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + "&7No zones created yet."));
            return;
        }

        sender.sendMessage(plugin.getConfigManager().colorize("&6&l=== RTP Zones ==="));

        for (RtpZone zone : zones) {
            String status = zone.isTeleportInProgress() ? "&aTeleporting" :
                    zone.isWaitingForPlayers() ? "&eWaiting" : "&7Idle";

            sender.sendMessage(plugin.getConfigManager().colorize(
                    "&e" + zone.getName() + " &7- Target: &f" + zone.getConfig().getTargetWorld() +
                            " &7- Players: &f" + zone.getPlayerCount() + "/" + zone.getConfig().getMaxPlayers() +
                            " &7- Status: " + status));
        }
    }
    private void handleDebug(CommandSender sender) {
        if (!sender.hasPermission("rtpzonex.admin")) {
            String msg = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("no-permission");
            sender.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }

        boolean currentState = github.mukulx.rtpzonex.utils.Debug.isEnabled();
        boolean newState = !currentState;
        github.mukulx.rtpzonex.utils.Debug.setEnabled(newState);

        String status = newState ? "&aEnabled" : "&cDisabled";
        sender.sendMessage(plugin.getConfigManager().colorize(
                plugin.getConfigManager().getPrefix() + "&7Debug mode: " + status));

        if (newState) {
            sender.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + "&7Debug logs will now appear in console"));
        }
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                 @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = Arrays.asList("help", "reload", "info", "zones", "gui", "debug");
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
