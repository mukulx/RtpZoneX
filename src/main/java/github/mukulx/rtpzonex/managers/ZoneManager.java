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
import github.mukulx.rtpzonex.zone.RtpZone;
import github.mukulx.rtpzonex.zone.ZoneConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZoneManager {

    private final RtpZoneX plugin;
    private final Map<String, RtpZone> zones;
    private final Map<UUID, Location> pos1Selections;
    private final Map<UUID, Location> pos2Selections;
    private File zonesFile;
    private FileConfiguration zonesConfig;
    public ZoneManager(RtpZoneX plugin) {
        this.plugin = plugin;
        this.zones = new HashMap<>();
        this.pos1Selections = new HashMap<>();
        this.pos2Selections = new HashMap<>();
    }
    public void loadZones() {
        zonesFile = new File(plugin.getDataFolder(), "zones.yml");
        if (!zonesFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                zonesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create zones.yml!");
                e.printStackTrace();
            }
        }
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
        zones.clear();
        if (zonesConfig.contains("zones")) {
            for (String zoneName : zonesConfig.getConfigurationSection("zones").getKeys(false)) {
                String path = "zones." + zoneName;
                String worldName = zonesConfig.getString(path + ".world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("World " + worldName + " not found for zone " + zoneName);
                    continue;
                }
                double x1 = zonesConfig.getDouble(path + ".x1");
                double y1 = zonesConfig.getDouble(path + ".y1");
                double z1 = zonesConfig.getDouble(path + ".z1");
                double x2 = zonesConfig.getDouble(path + ".x2");
                double y2 = zonesConfig.getDouble(path + ".y2");
                double z2 = zonesConfig.getDouble(path + ".z2");
                Location pos1 = new Location(world, x1, y1, z1);
                Location pos2 = new Location(world, x2, y2, z2);
                RtpZone zone = new RtpZone(zoneName, pos1, pos2);
                loadZoneConfig(zone, path + ".config");
                zones.put(zoneName.toLowerCase(), zone);
                plugin.getLogger().info("Loaded zone: " + zoneName);
            }
        }
    }
    private void loadZoneConfig(RtpZone zone, String path) {
        ZoneConfig config = zone.getConfig();
        if (zonesConfig.contains(path)) {
            config.setTargetWorld(zonesConfig.getString(path + ".target-world", "world"));
            config.setMinDistance(zonesConfig.getInt(path + ".min-distance", 100));
            config.setMaxDistance(zonesConfig.getInt(path + ".max-distance", 5000));
            config.setTeleportCountdown(zonesConfig.getInt(path + ".teleport-countdown", 5));
            config.setMinPlayers(zonesConfig.getInt(path + ".min-players", 1));
            config.setMaxPlayers(zonesConfig.getInt(path + ".max-players", 10));
            config.setWaitForPlayers(zonesConfig.getBoolean(path + ".wait-for-players", true));
            config.setWaitTime(zonesConfig.getInt(path + ".wait-time", 10));
            config.setCooldown(zonesConfig.getInt(path + ".cooldown", 60));
            config.setDarknessEnabled(zonesConfig.getBoolean(path + ".darkness-enabled", false));
            config.setSoundEnabled(zonesConfig.getBoolean(path + ".sound-enabled", true));
            try {
                config.setTeleportSound(Sound.valueOf(zonesConfig.getString(path + ".teleport-sound", "ENTITY_ENDERMAN_TELEPORT")));
            } catch (IllegalArgumentException e) {
                config.setTeleportSound(Sound.ENTITY_ENDERMAN_TELEPORT);
            }
            config.setSoundVolume((float) zonesConfig.getDouble(path + ".sound-volume", 1.0));
            config.setSoundPitch((float) zonesConfig.getDouble(path + ".sound-pitch", 1.0));
            config.setParticlesEnabled(zonesConfig.getBoolean(path + ".particles-enabled", false));
            config.setZoneParticlesEnabled(zonesConfig.getBoolean(path + ".zone-particles-enabled", false));
            try {
                config.setParticleType(Particle.valueOf(zonesConfig.getString(path + ".particle-type", "PORTAL")));
            } catch (IllegalArgumentException e) {
                config.setParticleType(Particle.PORTAL);
            }
            config.setParticleCount(zonesConfig.getInt(path + ".particle-count", 50));
            config.setTitlesEnabled(zonesConfig.getBoolean(path + ".titles-enabled", true));
            config.setShowCoordinates(zonesConfig.getBoolean(path + ".show-coordinates", true));
            config.setMaxAttempts(zonesConfig.getInt(path + ".max-attempts", 50));
            config.setMinY(zonesConfig.getInt(path + ".min-y", -60));
            config.setMaxY(zonesConfig.getInt(path + ".max-y", 320));
            config.setCheckLava(zonesConfig.getBoolean(path + ".check-lava", true));
            config.setCheckWater(zonesConfig.getBoolean(path + ".check-water", true));
        }
    }
    public void saveZones() {
        if (zonesFile == null) {
            return;
        }
        zonesConfig = new YamlConfiguration();
        for (Map.Entry<String, RtpZone> entry : zones.entrySet()) {
            RtpZone zone = entry.getValue();
            String path = "zones." + zone.getName();
            zonesConfig.set(path + ".world", zone.getWorld().getName());
            zonesConfig.set(path + ".x1", zone.getPos1().getX());
            zonesConfig.set(path + ".y1", zone.getPos1().getY());
            zonesConfig.set(path + ".z1", zone.getPos1().getZ());
            zonesConfig.set(path + ".x2", zone.getPos2().getX());
            zonesConfig.set(path + ".y2", zone.getPos2().getY());
            zonesConfig.set(path + ".z2", zone.getPos2().getZ());
            saveZoneConfig(zone, path + ".config");
        }
        try {
            zonesConfig.save(zonesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save zones.yml!");
            e.printStackTrace();
        }
    }
    private void saveZoneConfig(RtpZone zone, String path) {
        ZoneConfig config = zone.getConfig();
        zonesConfig.set(path + ".target-world", config.getTargetWorld());
        zonesConfig.set(path + ".min-distance", config.getMinDistance());
        zonesConfig.set(path + ".max-distance", config.getMaxDistance());
        zonesConfig.set(path + ".teleport-countdown", config.getTeleportCountdown());
        zonesConfig.set(path + ".min-players", config.getMinPlayers());
        zonesConfig.set(path + ".max-players", config.getMaxPlayers());
        zonesConfig.set(path + ".wait-for-players", config.isWaitForPlayers());
        zonesConfig.set(path + ".wait-time", config.getWaitTime());
        zonesConfig.set(path + ".cooldown", config.getCooldown());
        zonesConfig.set(path + ".darkness-enabled", config.isDarknessEnabled());
        zonesConfig.set(path + ".sound-enabled", config.isSoundEnabled());
        zonesConfig.set(path + ".teleport-sound", config.getTeleportSound().toString());
        zonesConfig.set(path + ".sound-volume", config.getSoundVolume());
        zonesConfig.set(path + ".sound-pitch", config.getSoundPitch());
        zonesConfig.set(path + ".particles-enabled", config.isParticlesEnabled());
        zonesConfig.set(path + ".zone-particles-enabled", config.isZoneParticlesEnabled());
        zonesConfig.set(path + ".particle-type", config.getParticleType().name());
        zonesConfig.set(path + ".particle-count", config.getParticleCount());
        zonesConfig.set(path + ".titles-enabled", config.isTitlesEnabled());
        zonesConfig.set(path + ".show-coordinates", config.isShowCoordinates());
        zonesConfig.set(path + ".max-attempts", config.getMaxAttempts());
        zonesConfig.set(path + ".min-y", config.getMinY());
        zonesConfig.set(path + ".max-y", config.getMaxY());
        zonesConfig.set(path + ".check-lava", config.isCheckLava());
        zonesConfig.set(path + ".check-water", config.isCheckWater());
    }
    public boolean createZone(String name, Location pos1, Location pos2) {
        if (zones.containsKey(name.toLowerCase())) {
            return false;
        }
        RtpZone zone = new RtpZone(name, pos1, pos2);
        zones.put(name.toLowerCase(), zone);
        saveZones();
        plugin.getHologramManager().createHologram(zone);
        return true;
    }
    public boolean deleteZone(String name) {
        if (!zones.containsKey(name.toLowerCase())) {
            return false;
        }
        plugin.getHologramManager().removeHologram(name);
        zones.remove(name.toLowerCase());
        saveZones();
        return true;
    }
    public RtpZone getZone(String name) {
        return zones.get(name.toLowerCase());
    }
    public Collection<RtpZone> getAllZones() {
        return zones.values();
    }
    public RtpZone getZoneAt(Location location) {
        for (RtpZone zone : zones.values()) {
            if (zone.contains(location)) {
                return zone;
            }
        }
        return null;
    }
    public void setPos1(Player player, Location location) {
        pos1Selections.put(player.getUniqueId(), location);
    }
    public void setPos2(Player player, Location location) {
        pos2Selections.put(player.getUniqueId(), location);
    }
    public Location getPos1(Player player) {
        return pos1Selections.get(player.getUniqueId());
    }
    public Location getPos2(Player player) {
        return pos2Selections.get(player.getUniqueId());
    }
    public boolean hasSelection(Player player) {
        return pos1Selections.containsKey(player.getUniqueId())
                && pos2Selections.containsKey(player.getUniqueId());
    }
    public void clearSelection(Player player) {
        pos1Selections.remove(player.getUniqueId());
        pos2Selections.remove(player.getUniqueId());
    }
    public int getZoneCount() {
        return zones.size();
    }
    public Map<String, Object> getHologramConfig(String zoneName) {
        if (zonesConfig == null) {
            return null;
        }

        String path = "zones." + zoneName.toLowerCase() + ".hologram";
        if (!zonesConfig.contains(path)) {
            return null;
        }

        Map<String, Object> config = new HashMap<>();
        config.put("enabled", zonesConfig.getBoolean(path + ".enabled", false));
        config.put("height-offset", zonesConfig.getDouble(path + ".height-offset", 2.5));
        config.put("lines", zonesConfig.getStringList(path + ".lines"));

        return config;
    }
    public int getHologramUpdateInterval() {
        if (zonesConfig == null) {
            return 20;
        }

        for (String zoneName : zones.keySet()) {
            String path = "zones." + zoneName + ".hologram.update-interval";
            if (zonesConfig.contains(path)) {
                return zonesConfig.getInt(path, 20);
            }
        }

        return 20;
    }
}
