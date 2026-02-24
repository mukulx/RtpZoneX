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
import github.mukulx.rtpzonex.gui.ZoneListGUI;
import github.mukulx.rtpzonex.zone.RtpZone;
import github.mukulx.rtpzonex.zone.ZoneConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {
    private final RtpZoneX plugin;
    private final Map<UUID, RtpZone> editingZone = new HashMap<>();
    public GUIListener(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().title().toString();

        if (title.contains("RTP Zones") || title.contains("Zone Config") ||
                title.contains("Select Target World") || title.contains("Select Particle") ||
                title.contains("Select Sound")) {
            event.setCancelled(true);
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (title.contains("RTP Zones")) {
            handleZoneListClick(player, event);
            return;
        }

        if (title.contains("Zone Config")) {
            handleZoneConfigClick(player, event);
            return;
        }

        if (title.contains("Select Target World")) {
            handleWorldSelectorClick(player, event);
            return;
        }

        if (title.contains("Select Particle")) {
            handleParticleSelectorClick(player, event);
            return;
        }

        if (title.contains("Select Sound")) {
            handleSoundSelectorClick(player, event);
            return;
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().title().toString();
        if (title.contains("Zone Config")) {
            RtpZone zone = editingZone.get(player.getUniqueId());
            if (zone != null) {
                plugin.getZoneManager().saveZones();
            }
        }
    }
    private void handleZoneListClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (item.getType() == Material.BOOK) {
            return;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        for (RtpZone zone : plugin.getZoneManager().getAllZones()) {
            String itemName = item.getItemMeta().displayName().toString().toLowerCase();
            if (itemName.contains(zone.getName().toLowerCase())) {
                editingZone.put(player.getUniqueId(), zone);
                player.closeInventory();
                github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                    new ZoneConfigGUI(plugin, zone, player).openMainMenu();
                }, 2L);
                return;
            }
        }
    }
    private void handleZoneConfigClick(Player player, InventoryClickEvent event) {
        RtpZone zone = editingZone.get(player.getUniqueId());
        if (zone == null) {
            player.closeInventory();
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        ZoneConfig config = zone.getConfig();
        int slot = event.getSlot();
        ClickType click = event.getClick();
        boolean shift = click.isShiftClick();
        boolean left = click.isLeftClick();
        boolean right = click.isRightClick();
        switch (slot) {
            case 10 -> {
                player.closeInventory();
                github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                    new ZoneConfigGUI(plugin, zone, player).openWorldSelector();
                }, 2L);
            }
            case 11 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.MIN_DISTANCE);
            }
            case 12 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.MAX_DISTANCE);
            }
            case 14 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.TELEPORT_COUNTDOWN);
            }
            case 15 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.COOLDOWN);
            }
            case 16 -> {
                config.setShowCoordinates(!config.isShowCoordinates());
                player.sendMessage(plugin.getConfigManager().colorize("&7Show Coordinates: " + (config.isShowCoordinates() ? "&aEnabled" : "&cDisabled")));
                refreshGUI(player, zone);
            }
            case 19 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.MIN_PLAYERS);
            }
            case 20 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.MAX_PLAYERS);
            }
            case 22 -> {
                config.setWaitForPlayers(!config.isWaitForPlayers());
                player.sendMessage(plugin.getConfigManager().colorize("&7Wait for Players: " + (config.isWaitForPlayers() ? "&aEnabled" : "&cDisabled")));
                refreshGUI(player, zone);
            }
            case 23 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.WAIT_TIME);
            }
            case 25 -> {
                plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.PREFIX);
            }
            case 28 -> {
                config.setDarknessEnabled(!config.isDarknessEnabled());
                player.sendMessage(plugin.getConfigManager().colorize("&7Darkness: " + (config.isDarknessEnabled() ? "&aEnabled" : "&cDisabled")));
                refreshGUI(player, zone);
            }
            case 30 -> {
                if (right) {
                    player.closeInventory();
                    github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                        new ZoneConfigGUI(plugin, zone, player).openSoundSelector();
                    }, 2L);
                } else {
                    config.setSoundEnabled(!config.isSoundEnabled());
                    player.sendMessage(plugin.getConfigManager().colorize("&7Sound: " + (config.isSoundEnabled() ? "&aEnabled" : "&cDisabled")));
                    refreshGUI(player, zone);
                }
            }
            case 32 -> {
                if (right) {
                    player.closeInventory();
                    github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                        new ZoneConfigGUI(plugin, zone, player).openParticleSelector();
                    }, 2L);
                } else if (shift) {
                    plugin.getChatInputListener().requestInput(player, zone, ChatInputListener.InputType.PARTICLE_COUNT);
                } else {
                    config.setParticlesEnabled(!config.isParticlesEnabled());
                    player.sendMessage(plugin.getConfigManager().colorize("&7Teleport Particles: " + (config.isParticlesEnabled() ? "&aEnabled" : "&cDisabled")));
                    refreshGUI(player, zone);
                }
            }
            case 33 -> {
                config.setZoneParticlesEnabled(!config.isZoneParticlesEnabled());
                player.sendMessage(plugin.getConfigManager().colorize("&7Zone Particles: " + (config.isZoneParticlesEnabled() ? "&aEnabled" : "&cDisabled")));
                refreshGUI(player, zone);
            }
            case 34 -> {
                config.setTitlesEnabled(!config.isTitlesEnabled());
                player.sendMessage(plugin.getConfigManager().colorize("&7Titles: " + (config.isTitlesEnabled() ? "&aEnabled" : "&cDisabled")));
                refreshGUI(player, zone);
            }
            case 37 -> {
                config.setCheckLava(!config.isCheckLava());
                player.sendMessage(plugin.getConfigManager().colorize("&7Check Lava: " + (config.isCheckLava() ? "&aEnabled" : "&cDisabled")));
                refreshGUI(player, zone);
            }
            case 39 -> {
                config.setCheckWater(!config.isCheckWater());
                player.sendMessage(plugin.getConfigManager().colorize("&7Check Water: " + (config.isCheckWater() ? "&aEnabled" : "&cDisabled")));
                refreshGUI(player, zone);
            }
            case 41 -> {
                if (shift) {
                    int change = left ? 10 : -10;
                    config.setMaxY(Math.max(config.getMinY() + 10, Math.min(320, config.getMaxY() + change)));
                    player.sendMessage(plugin.getConfigManager().colorize("&7Max Y: &e" + config.getMaxY()));
                } else {
                    int change = left ? 10 : -10;
                    config.setMinY(Math.max(-64, Math.min(config.getMaxY() - 10, config.getMinY() + change)));
                    player.sendMessage(plugin.getConfigManager().colorize("&7Min Y: &e" + config.getMinY()));
                }
                refreshGUI(player, zone);
            }
            case 49 -> {
                plugin.getZoneManager().saveZones();
                editingZone.remove(player.getUniqueId());
                player.closeInventory();
                player.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + "&aZone configuration saved!"));
            }
        }
    }
    private void handleWorldSelectorClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        RtpZone zone = editingZone.get(player.getUniqueId());
        if (zone == null) {
            player.closeInventory();
            return;
        }
        if (item.getType() == Material.ARROW) {
            player.closeInventory();
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                new ZoneConfigGUI(plugin, zone, player).openMainMenu();
            }, 2L);
            return;
        }
        int slot = event.getSlot();
        java.util.List<World> worlds = Bukkit.getWorlds();
        if (slot >= 0 && slot < worlds.size()) {
            World selectedWorld = worlds.get(slot);
            zone.getConfig().setTargetWorld(selectedWorld.getName());
            player.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + "&aTarget world set to: &e" + selectedWorld.getName()));
            player.closeInventory();
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                new ZoneConfigGUI(plugin, zone, player).openMainMenu();
            }, 2L);
        }
    }
    private void handleParticleSelectorClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        RtpZone zone = editingZone.get(player.getUniqueId());
        if (zone == null) {
            player.closeInventory();
            return;
        }
        if (item.getType() == Material.ARROW) {
            player.closeInventory();
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                new ZoneConfigGUI(plugin, zone, player).openMainMenu();
            }, 2L);
            return;
        }
        if (item.getType() != Material.LIME_STAINED_GLASS_PANE &&
                item.getType() != Material.WHITE_STAINED_GLASS_PANE) {
            return;
        }
        Particle[] particles = ZoneConfigGUI.PARTICLE_OPTIONS;
        int slot = event.getSlot();
        if (slot >= 0 && slot < particles.length) {
            Particle selectedParticle = particles[slot];
            zone.getConfig().setParticleType(selectedParticle);
            zone.getConfig().setParticlesEnabled(true);
            try {
                spawnParticleSafe(player.getWorld(), selectedParticle, player.getLocation().add(0, 1, 0), 30);
            } catch (Exception ignored) {}
            player.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + "&aParticle set to: &e" + selectedParticle.name()));
            plugin.getZoneManager().saveZones();
            player.closeInventory();
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                new ZoneConfigGUI(plugin, zone, player).openMainMenu();
            }, 2L);
        }
    }
    private void spawnParticleSafe(org.bukkit.World world, Particle particle, org.bukkit.Location location, int count) {
        try {
            switch (particle) {
                case DUST -> world.spawnParticle(particle, location, count, 0.5, 0.5, 0.5, 0,
                        new Particle.DustOptions(org.bukkit.Color.PURPLE, 1.0f));
                case DUST_COLOR_TRANSITION -> world.spawnParticle(particle, location, count, 0.5, 0.5, 0.5, 0,
                        new Particle.DustTransition(org.bukkit.Color.PURPLE, org.bukkit.Color.BLUE, 1.0f));
                default -> world.spawnParticle(particle, location, count, 0.5, 0.5, 0.5, 0);
            }
        } catch (Exception e) {
            try {
                world.spawnParticle(particle, location, count, 0.5, 0.5, 0.5, 0);
            } catch (Exception ignored) {
            }
        }
    }
    private void handleSoundSelectorClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        RtpZone zone = editingZone.get(player.getUniqueId());
        if (zone == null) {
            player.closeInventory();
            return;
        }
        if (item.getType() == Material.ARROW) {
            player.closeInventory();
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                new ZoneConfigGUI(plugin, zone, player).openMainMenu();
            }, 2L);
            return;
        }
        Sound[] sounds = ZoneConfigGUI.SOUND_OPTIONS;
        int slot = event.getSlot();
        if (slot >= 0 && slot < sounds.length) {
            Sound selectedSound = sounds[slot];
            zone.getConfig().setTeleportSound(selectedSound);
            zone.getConfig().setSoundEnabled(true);
            player.playSound(player.getLocation(), selectedSound, 1.0f, 1.0f);
            player.sendMessage(plugin.getConfigManager().colorize(
                    plugin.getConfigManager().getPrefix() + "&aSound set to: &e" + selectedSound.toString()));
            player.closeInventory();
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
                new ZoneConfigGUI(plugin, zone, player).openMainMenu();
            }, 2L);
        }
    }
    private void refreshGUI(Player player, RtpZone zone) {
        player.closeInventory();
        github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskLater(plugin, () -> {
            new ZoneConfigGUI(plugin, zone, player).openMainMenu();
        }, 1L);
    }
    public void setEditingZone(Player player, RtpZone zone) {
        editingZone.put(player.getUniqueId(), zone);
    }
    public void clearEditingZone(Player player) {
        editingZone.remove(player.getUniqueId());
    }
}
