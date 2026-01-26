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
import github.mukulx.rtpzonex.gui.ZoneConfigGUI;
import github.mukulx.rtpzonex.gui.ZoneListGUI;
import github.mukulx.rtpzonex.utils.ItemUtils;
import github.mukulx.rtpzonex.zone.RtpZone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RtpZoneCommand implements CommandExecutor, TabCompleter {
    private final RtpZoneX plugin;
    public RtpZoneCommand(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    private boolean hasWand(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.BLAZE_ROD) {
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    String displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                            .serialize(item.getItemMeta().displayName());
                    if (displayName.contains("RTP Zone Wand")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("player-only")));
            return true;
        }

        if (!player.hasPermission("rtpzonex.zone.manage")) {
            String msg = plugin.getConfigManager().getPrefix() +
                    plugin.getConfigManager().getMessage("no-permission");
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return true;
        }

        if (args.length == 0) {
            if (!hasWand(player)) {
                player.getInventory().addItem(ItemUtils.createZoneWand(plugin));
                String msg = plugin.getConfigManager().getPrefix() +
                        plugin.getConfigManager().getMessage("wand-given");
                player.sendMessage(plugin.getConfigManager().colorize(msg));
            }
            new ZoneListGUI(plugin, player).open();
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "wand":
                handleWand(player);
                break;
            case "create":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().colorize(
                            plugin.getConfigManager().getPrefix() + "&cUsage: /rtpzone create <name>"));
                    return true;
                }
                handleCreate(player, args[1]);
                break;
            case "delete":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().colorize(
                            plugin.getConfigManager().getPrefix() + "&cUsage: /rtpzone delete <name>"));
                    return true;
                }
                handleDelete(player, args[1]);
                break;
            case "list":
                new ZoneListGUI(plugin, player).open();
                break;
            case "config":
            case "edit":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().colorize(
                            plugin.getConfigManager().getPrefix() + "&cUsage: /rtpzone config <name>"));
                    return true;
                }
                handleConfig(player, args[1]);
                break;
            case "info":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().colorize(
                            plugin.getConfigManager().getPrefix() + "&cUsage: /rtpzone info <name>"));
                    return true;
                }
                handleInfo(player, args[1]);
                break;
            case "reload":
                handleReload(player);
                break;
            case "help":
            default:
                sendHelp(player);
                break;
        }

        return true;
    }
    private void sendHelp(Player player) {
        player.sendMessage(plugin.getConfigManager().colorize("&6&l=== RtpZone Help ==="));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone &7- Open zone list GUI"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone wand &7- Get the zone selection wand"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone create <name> &7- Create a new zone"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone delete <name> &7- Delete a zone"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone list &7- Open zone list GUI"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone config <name> &7- Open zone config GUI"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone info <name> &7- Show zone info"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/rtpzone reload &7- Reload all configurations"));
    }
    private void handleWand(Player player) {
        player.getInventory().addItem(ItemUtils.createZoneWand(plugin));
        String msg = plugin.getConfigManager().getPrefix() +
                plugin.getConfigManager().getMessage("wand-given");
        player.sendMessage(plugin.getConfigManager().colorize(msg));
    }
    private void handleCreate(Player player, String name) {
        if (!plugin.getZoneManager().hasSelection(player)) {
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("select-both-positions");
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }
        Location pos1 = plugin.getZoneManager().getPos1(player);
        Location pos2 = plugin.getZoneManager().getPos2(player);
        if (!pos1.getWorld().equals(pos2.getWorld())) {
            player.sendMessage(plugin.getConfigManager().colorize(
            plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("same-world-required")));
            return;
        }
        if (plugin.getZoneManager().createZone(name, pos1, pos2)) {
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("zone-created")
            .replace("%zone%", name);
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            plugin.getZoneManager().clearSelection(player);
            RtpZone zone = plugin.getZoneManager().getZone(name);
            if (zone != null) {
                player.sendMessage(plugin.getConfigManager().colorize(
                plugin.getConfigManager().getPrefix() + "&7Opening configuration GUI..."));
                new ZoneConfigGUI(plugin, zone, player).openMainMenu();
            }
        } else {
            player.sendMessage(plugin.getConfigManager().colorize(
            plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("zone-exists")));
        }
    }
    private void handleDelete(Player player, String name) {
        if (plugin.getZoneManager().deleteZone(name)) {
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("zone-deleted")
            .replace("%zone%", name);
            player.sendMessage(plugin.getConfigManager().colorize(msg));
        } else {
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("zone-not-found")
            .replace("%zone%", name);
            player.sendMessage(plugin.getConfigManager().colorize(msg));
        }
    }
    private void handleConfig(Player player, String name) {
        RtpZone zone = plugin.getZoneManager().getZone(name);
        if (zone == null) {
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("zone-not-found")
            .replace("%zone%", name);
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }
        new ZoneConfigGUI(plugin, zone, player).openMainMenu();
    }
    private void handleInfo(Player player, String name) {
        RtpZone zone = plugin.getZoneManager().getZone(name);
        if (zone == null) {
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("zone-not-found")
            .replace("%zone%", name);
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }
        player.sendMessage(plugin.getConfigManager().colorize("&6&l=== Zone: " + zone.getName() + " ==="));
        player.sendMessage(plugin.getConfigManager().colorize("&7Zone World: &f" + zone.getWorld().getName()));
        player.sendMessage(plugin.getConfigManager().colorize("&7Target World: &f" + zone.getConfig().getTargetWorld()));
        player.sendMessage(plugin.getConfigManager().colorize("&7Position 1: &f" +
        zone.getPos1().getBlockX() + ", " + zone.getPos1().getBlockY() + ", " + zone.getPos1().getBlockZ()));
        player.sendMessage(plugin.getConfigManager().colorize("&7Position 2: &f" +
        zone.getPos2().getBlockX() + ", " + zone.getPos2().getBlockY() + ", " + zone.getPos2().getBlockZ()));
        player.sendMessage(plugin.getConfigManager().colorize("&7Players: &f" + zone.getPlayerCount() + "/" + zone.getConfig().getMaxPlayers()));
        player.sendMessage(plugin.getConfigManager().colorize("&7Distance: &f" + zone.getConfig().getMinDistance() + " - " + zone.getConfig().getMaxDistance()));
        player.sendMessage(plugin.getConfigManager().colorize("&7Countdown: &f" + zone.getConfig().getTeleportCountdown() + "s"));
        player.sendMessage(plugin.getConfigManager().colorize("&7Wait Time: &f" + zone.getConfig().getWaitTime() + "s"));
        player.sendMessage(plugin.getConfigManager().colorize("&7Particles: &f" + (zone.getConfig().isParticlesEnabled() ? zone.getConfig().getParticleType().name() : "Disabled")));
        player.sendMessage(plugin.getConfigManager().colorize("&7Status: &f" + (zone.isTeleportInProgress() ? "Teleporting" : zone.isWaitingForPlayers() ? "Waiting" : "Idle")));
    }
    private void handleReload(Player player) {
        if (!player.hasPermission("rtpzonex.reload")) {
            String msg = plugin.getConfigManager().getPrefix() +
            plugin.getConfigManager().getMessage("no-permission");
            player.sendMessage(plugin.getConfigManager().colorize(msg));
            return;
        }
        player.sendMessage(plugin.getConfigManager().colorize(
        plugin.getConfigManager().getPrefix() + "&7Reloading RtpZoneX..."));
        plugin.reload();
        player.sendMessage(plugin.getConfigManager().colorize(
        plugin.getConfigManager().getPrefix() + "&aSuccessfully reloaded all configurations!"));
        player.sendMessage(plugin.getConfigManager().colorize(
        plugin.getConfigManager().getPrefix() + "&7Ã¢â‚¬Â¢ Config.yml"));
        player.sendMessage(plugin.getConfigManager().colorize(
        plugin.getConfigManager().getPrefix() + "&7Ã¢â‚¬Â¢ Messages.yml"));
        player.sendMessage(plugin.getConfigManager().colorize(
        plugin.getConfigManager().getPrefix() + "&7Ã¢â‚¬Â¢ GUI.yml"));
        player.sendMessage(plugin.getConfigManager().colorize(
        plugin.getConfigManager().getPrefix() + "&7Ã¢â‚¬Â¢ Zones.yml"));
        player.sendMessage(plugin.getConfigManager().colorize(
        plugin.getConfigManager().getPrefix() + "&7Ã¢â‚¬Â¢ Holograms"));
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
    @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = Arrays.asList("wand", "create", "delete", "list", "config", "info", "reload", "help");
            return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
            .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("delete") || subCommand.equals("info") || subCommand.equals("config") || subCommand.equals("edit")) {
                return plugin.getZoneManager().getAllZones().stream()
                .map(RtpZone::getName)
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}
