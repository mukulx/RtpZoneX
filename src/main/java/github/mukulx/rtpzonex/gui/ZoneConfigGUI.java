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
package github.mukulx.rtpzonex.gui;

import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.zone.RtpZone;
import github.mukulx.rtpzonex.zone.ZoneConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ZoneConfigGUI {
    private final RtpZoneX plugin;
    private final RtpZone zone;
    private final Player player;
    public static final Particle[] PARTICLE_OPTIONS = {
            Particle.PORTAL, Particle.FLAME, Particle.SOUL_FIRE_FLAME,
            Particle.HEART, Particle.HAPPY_VILLAGER, Particle.CLOUD,
            Particle.SMOKE, Particle.ENCHANT, Particle.END_ROD,
            Particle.FIREWORK, Particle.WITCH, Particle.TOTEM_OF_UNDYING,
            Particle.DRAGON_BREATH, Particle.CHERRY_LEAVES, Particle.GLOW
    };

    public static final Sound[] SOUND_OPTIONS = {
            Sound.ENTITY_ENDERMAN_TELEPORT, Sound.ENTITY_ENDER_PEARL_THROW,
            Sound.BLOCK_PORTAL_TRAVEL, Sound.BLOCK_BEACON_ACTIVATE,
            Sound.ENTITY_PLAYER_LEVELUP, Sound.BLOCK_NOTE_BLOCK_PLING,
            Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_AMETHYST_BLOCK_CHIME,
            Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, Sound.BLOCK_END_PORTAL_SPAWN
    };
    public ZoneConfigGUI(RtpZoneX plugin, RtpZone zone, Player player) {
        this.plugin = plugin;
        this.zone = zone;
        this.player = player;
        plugin.getGUIListener().setEditingZone(player, zone);
    }
    public void openMainMenu() {
        Component title = plugin.getConfigManager().colorize("&6Zone Config: " + zone.getName());
        Inventory inv = Bukkit.createInventory(null, 54, title);
        ZoneConfig config = zone.getConfig();
        inv.setItem(10, createItem(Material.GRASS_BLOCK, "&a&lTarget World",
                "&7Current: &e" + config.getTargetWorld(),
                "",
                "&eClick to change"));

        inv.setItem(11, createItem(Material.COMPASS, "&b&lMin Distance",
                "&7Current: &e" + config.getMinDistance(),
                "",
                "&eClick to type a value"));

        inv.setItem(12, createItem(Material.SPYGLASS, "&b&lMax Distance",
                "&7Current: &e" + config.getMaxDistance(),
                "",
                "&eClick to type a value"));

        inv.setItem(14, createItem(Material.CLOCK, "&e&lTeleport Countdown",
                "&7Current: &e" + config.getTeleportCountdown() + "s",
                "",
                "&eClick to type a value"));

        inv.setItem(15, createItem(Material.SNOWBALL, "&c&lCooldown",
                "&7Current: &e" + config.getCooldown() + "s",
                "",
                "&eClick to type a value"));

        inv.setItem(16, createItem(
                config.isShowCoordinates() ? Material.MAP : Material.PAPER,
                "&f&lShow Coordinates",
                "&7Current: " + (config.isShowCoordinates() ? "&aEnabled" : "&cDisabled"),
                "",
                "&eClick to toggle"));

        inv.setItem(19, createItem(Material.PLAYER_HEAD, "&d&lMin Players",
                "&7Current: &e" + config.getMinPlayers(),
                "",
                "&eClick to type a value"));

        inv.setItem(20, createItem(Material.SKELETON_SKULL, "&d&lMax Players",
                "&7Current: &e" + config.getMaxPlayers(),
                "",
                "&eClick to type a value"));

        inv.setItem(22, createItem(
                config.isWaitForPlayers() ? Material.LIME_DYE : Material.GRAY_DYE,
                "&6&lWait for Players",
                "&7Current: " + (config.isWaitForPlayers() ? "&aEnabled" : "&cDisabled"),
                "&7When enabled, waits for more players",
                "&7before teleporting the group.",
                "",
                "&eClick to toggle"));

        inv.setItem(23, createItem(Material.HOPPER, "&6&lWait Time",
                "&7Current: &e" + config.getWaitTime() + "s",
                "&7Time to wait for more players",
                "",
                "&eClick to type a value"));

        inv.setItem(25, createItem(Material.NAME_TAG, "&6&lMessage Prefix",
                "&7Current: " + plugin.getConfigManager().getPrefix(),
                "",
                "&eClick to change"));

        inv.setItem(28, createItem(
                config.isDarknessEnabled() ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE,
                "&8&lDarkness Effect",
                "&7Current: " + (config.isDarknessEnabled() ? "&aEnabled" : "&cDisabled"),
                "",
                "&eClick to toggle"));

        inv.setItem(30, createItem(
                config.isSoundEnabled() ? Material.NOTE_BLOCK : Material.BARRIER,
                "&9&lTeleport Sound",
                "&7Enabled: " + (config.isSoundEnabled() ? "&aYes" : "&cNo"),
                "&7Sound: &e" + config.getTeleportSound().toString(),
                "",
                "&eLeft-click: &fToggle on/off",
                "&eRight-click: &fChange sound"));

        inv.setItem(32, createItem(
                config.isParticlesEnabled() ? Material.FIREWORK_ROCKET : Material.BARRIER,
                "&5&lTeleport Particles",
                "&7Enabled: " + (config.isParticlesEnabled() ? "&aYes" : "&cNo"),
                "&7Type: &e" + config.getParticleType().name(),
                "&7Count: &e" + config.getParticleCount(),
                "",
                "&eLeft-click: &fToggle on/off",
                "&eRight-click: &fChange particle",
                "&eShift+click: &fSet count"));

        inv.setItem(33, createItem(
                config.isZoneParticlesEnabled() ? Material.BLAZE_POWDER : Material.GUNPOWDER,
                "&d&lZone Particles",
                "&7Current: " + (config.isZoneParticlesEnabled() ? "&aEnabled" : "&cDisabled"),
                "&7Shows particles in the RTP zone",
                "",
                "&eClick to toggle"));

        inv.setItem(34, createItem(
                config.isTitlesEnabled() ? Material.OAK_SIGN : Material.BARRIER,
                "&f&lTitles",
                "&7Current: " + (config.isTitlesEnabled() ? "&aEnabled" : "&cDisabled"),
                "",
                "&eClick to toggle"));

        inv.setItem(37, createItem(
                config.isCheckLava() ? Material.LAVA_BUCKET : Material.BUCKET,
                "&c&lCheck Lava",
                "&7Current: " + (config.isCheckLava() ? "&aEnabled" : "&cDisabled"),
                "",
                "&eClick to toggle"));

        inv.setItem(39, createItem(
                config.isCheckWater() ? Material.WATER_BUCKET : Material.BUCKET,
                "&b&lCheck Water",
                "&7Current: " + (config.isCheckWater() ? "&aEnabled" : "&cDisabled"),
                "",
                "&eClick to toggle"));

        inv.setItem(41, createItem(Material.BEDROCK, "&7&lY Range",
                "&7Min Y: &e" + config.getMinY(),
                "&7Max Y: &e" + config.getMaxY(),
                "",
                "&aLeft-click: &fMin Y +10",
                "&cRight-click: &fMin Y -10",
                "&aShift+Left: &fMax Y +10",
                "&cShift+Right: &fMax Y -10"));

        inv.setItem(49, createItem(Material.EMERALD_BLOCK, "&a&lSave & Close",
                "&7Click to save and close"));
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }
        player.openInventory(inv);
    }
    public void openWorldSelector() {
        List<World> worlds = Bukkit.getWorlds();
        int size = Math.max(18, ((worlds.size() / 9) + 2) * 9);
        if (size > 54) size = 54;
        Component title = plugin.getConfigManager().colorize("&6Select Target World");
        Inventory inv = Bukkit.createInventory(null, size, title);
        int slot = 0;
        for (World world : worlds) {
            if (slot >= size - 9) break;
            Material mat = switch (world.getEnvironment()) {
                case NETHER -> Material.NETHERRACK;
                case THE_END -> Material.END_STONE;
                default -> Material.GRASS_BLOCK;
            };
            boolean selected = world.getName().equals(zone.getConfig().getTargetWorld());
            inv.setItem(slot++, createItem(mat,
            (selected ? "&aÃ¢Å“â€œ " : "&e") + world.getName(),
            "&7Environment: &f" + world.getEnvironment().name(),
            "",
            selected ? "&aÃ¢Å“â€œ Currently selected" : "&eClick to select"));
        }
        inv.setItem(size - 1, createItem(Material.ARROW, "&c&lBack", "&7Return to main menu"));
        player.openInventory(inv);
    }
    public void openParticleSelector() {
        Component title = plugin.getConfigManager().colorize("&6Select Particle");
        Inventory inv = Bukkit.createInventory(null, 27, title);
        int slot = 0;
        for (Particle particle : PARTICLE_OPTIONS) {
            if (slot >= 26) break;
            boolean selected = particle == zone.getConfig().getParticleType();
            inv.setItem(slot++, createItem(
            selected ? Material.LIME_STAINED_GLASS_PANE : Material.WHITE_STAINED_GLASS_PANE,
            (selected ? "&aÃ¢Å“â€œ " : "&e") + particle.name(),
            "",
            selected ? "&aÃ¢Å“â€œ Currently selected" : "&eClick to select"));
        }
        inv.setItem(26, createItem(Material.ARROW, "&c&lBack", "&7Return to main menu"));
        player.openInventory(inv);
    }
    public void openSoundSelector() {
        Component title = plugin.getConfigManager().colorize("&6Select Sound");
        Inventory inv = Bukkit.createInventory(null, 18, title);
        int slot = 0;
        for (Sound sound : SOUND_OPTIONS) {
            if (slot >= 17) break;
            boolean selected = sound == zone.getConfig().getTeleportSound();
            inv.setItem(slot++, createItem(
            selected ? Material.LIME_STAINED_GLASS_PANE : Material.NOTE_BLOCK,
            (selected ? "&aÃ¢Å“â€œ " : "&e") + formatSoundName(sound),
            "",
            selected ? "&aÃ¢Å“â€œ Currently selected" : "&eClick to select & preview"));
        }
        inv.setItem(17, createItem(Material.ARROW, "&c&lBack", "&7Return to main menu"));
        player.openInventory(inv);
    }
    private String formatSoundName(Sound sound) {
        String name = sound.toString();
        return name.replace("_", " ");
    }
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(plugin.getConfigManager().colorize(name));

            if (lore.length > 0) {
                List<Component> loreComponents = new ArrayList<>();
                for (String line : lore) {
                    loreComponents.add(plugin.getConfigManager().colorize(line));
                }
                meta.lore(loreComponents);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
    public RtpZone getZone() {
        return zone;
    }
}
