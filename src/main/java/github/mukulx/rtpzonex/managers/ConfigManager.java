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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {

    private final RtpZoneX plugin;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;
    private FileConfiguration guiConfig;
    private File messagesFile;
    private File guiFile;
    private ConfigUpdater configUpdater;
    public ConfigManager(RtpZoneX plugin) {
        this.plugin = plugin;
        this.configUpdater = new ConfigUpdater(plugin);
    }
    public void loadConfig() {
        configUpdater.updateAllConfigs();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        loadMessagesConfig();
        loadGuiConfig();

        configUpdater.cleanupOldBackups(5);
    }
    private void loadMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    private void loadGuiConfig() {
        guiFile = new File(plugin.getDataFolder(), "gui.yml");
        if (!guiFile.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
    }
    public void saveMessagesConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml!");
            e.printStackTrace();
        }
    }
    public void saveGuiConfig() {
        try {
            guiConfig.save(guiFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save gui.yml!");
            e.printStackTrace();
        }
    }
    public FileConfiguration getConfig() {
        return config;
    }
    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }
    public int getTeleportCountdown() {
        return config.getInt("general.teleport-countdown", 3);
    }
    public int getMinDistance() {
        return config.getInt("general.min-distance", 100);
    }
    public int getMaxDistance() {
        return config.getInt("general.max-distance", 5000);
    }
    public int getCooldown() {
        return config.getInt("general.cooldown", 60);
    }
    public int getMaxGroupSize() {
        return config.getInt("general.max-group-size", 10);
    }
    public int getMaxAttempts() {
        return config.getInt("safe-location.max-attempts", 50);
    }
    public boolean checkLava() {
        return config.getBoolean("safe-location.check-lava", true);
    }
    public boolean checkWater() {
        return config.getBoolean("safe-location.check-water", true);
    }
    public int getMinY() {
        return config.getInt("safe-location.min-y", -60);
    }
    public int getMaxY() {
        return config.getInt("safe-location.max-y", 320);
    }
    public List<String> getUnsafeBlocks() {
        return config.getStringList("safe-location.unsafe-blocks");
    }
    public boolean isDarknessEnabled() {
        return config.getBoolean("effects.darkness-enabled", true);
    }
    public int getDarknessAmplifier() {
        return config.getInt("effects.darkness-amplifier", 0);
    }
    public boolean isSoundEnabled() {
        return config.getBoolean("effects.sound-enabled", true);
    }
    public Sound getTeleportSound() {
        try {
            return Sound.valueOf(config.getString("effects.teleport-sound", "ENTITY_ENDERMAN_TELEPORT"));
        } catch (IllegalArgumentException e) {
            return Sound.ENTITY_ENDERMAN_TELEPORT;
        }
    }
    public float getSoundVolume() {
        return (float) config.getDouble("effects.sound-volume", 1.0);
    }
    public float getSoundPitch() {
        return (float) config.getDouble("effects.sound-pitch", 1.0);
    }
    public boolean isParticlesEnabled() {
        return config.getBoolean("effects.particles-enabled", true);
    }
    public String getParticleType() {
        return config.getString("effects.particle-type", "PORTAL");
    }
    public int getParticleCount() {
        return config.getInt("effects.particle-count", 50);
    }
    public boolean isCountdownTitleEnabled() {
        return config.getBoolean("titles.countdown-title-enabled", true);
    }
    public String getCountdownTitle() {
        return messagesConfig.getString("titles.countdown.title", "&5&lTeleporting...");
    }
    public String getCountdownSubtitle() {
        return messagesConfig.getString("titles.countdown.subtitle", "&7You and &e%players% &7will teleport in &c%countdown%s");
    }
    public int getTitleFadeIn() {
        return config.getInt("titles.fade-in", 10);
    }
    public int getTitleStay() {
        return config.getInt("titles.stay", 40);
    }
    public int getTitleFadeOut() {
        return config.getInt("titles.fade-out", 10);
    }
    public String getTeleportTitle() {
        return messagesConfig.getString("titles.teleported.title", "&a&lTeleported!");
    }
    public String getTeleportSubtitle() {
        return messagesConfig.getString("titles.teleported.subtitle", "&7You arrived at a random location");
    }
    public boolean isActionBarEnabled() {
        return config.getBoolean("actionbar.enabled", true);
    }
    public String getActionBarCountdown() {
        return messagesConfig.getString("actionbar.countdown", "&eÃ¢ÂÂ± &7Teleporting in &c%countdown% &7seconds...");
    }
    public String getActionBarTeleport() {
        return messagesConfig.getString("actionbar.teleported", "&aÃ¢Å“â€œ &7Successfully teleported!");
    }
    public Material getWandMaterial() {
        try {
            return Material.valueOf(config.getString("zone-wand.material", "BLAZE_ROD"));
        } catch (IllegalArgumentException e) {
            return Material.BLAZE_ROD;
        }
    }
    public String getWandName() {
        return config.getString("zone-wand.name", "&6&lRTP Zone Wand");
    }
    public List<String> getWandLore() {
        return config.getStringList("zone-wand.lore");
    }
    public boolean isWandGlowing() {
        return config.getBoolean("zone-wand.glowing", true);
    }
    public String getPrefix() {
        return messagesConfig.getString("prefix", "&8[&6RtpZoneX&8] &r");
    }
    public void setPrefix(String prefix) {
        messagesConfig.set("prefix", prefix);
        saveMessagesConfig();
    }
    public String getMessage(String key) {
        return messagesConfig.getString("messages." + key, "&cMessage not found: " + key);
    }
    public String getGuiString(String path, String def) {
        return guiConfig.getString(path, def);
    }
    public int getGuiInt(String path, int def) {
        return guiConfig.getInt(path, def);
    }
    public List<String> getGuiStringList(String path) {
        return guiConfig.getStringList(path);
    }
    public String getStatusEnabled() {
        return guiConfig.getString("status.enabled", "&aEnabled");
    }
    public String getStatusDisabled() {
        return guiConfig.getString("status.disabled", "&cDisabled");
    }
    public List<String> getAllowedWorlds() {
        return config.getStringList("worlds.allowed-worlds");
    }
    public int getWorldMinDistance(String world) {
        return config.getInt("worlds.world-settings." + world + ".min-distance", getMinDistance());
    }
    public int getWorldMaxDistance(String world) {
        return config.getInt("worlds.world-settings." + world + ".max-distance", getMaxDistance());
    }
    public Component colorize(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
    public String colorizeString(String text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(
                LegacyComponentSerializer.legacyAmpersand().deserialize(text)
        );
    }
    public ConfigUpdater getConfigUpdater() {
        return configUpdater;
    }
}
