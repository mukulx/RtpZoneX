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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigUpdater {

    private final RtpZoneX plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    public ConfigUpdater(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    public boolean updateConfig(String fileName, boolean useVersioning) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        github.mukulx.rtpzonex.utils.Debug.log("CONFIG", "Checking " + fileName + " for updates");
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
            github.mukulx.rtpzonex.utils.Debug.log("CONFIG", "Created new " + fileName);
            return true;
        }
        try {
            FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(configFile);
            InputStream defaultStream = plugin.getResource(fileName);
            if (defaultStream == null) {
                github.mukulx.rtpzonex.utils.Debug.error("CONFIG", "Could not find default " + fileName + " in JAR");
                return false;
            }
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
            new InputStreamReader(defaultStream));
            if (useVersioning) {
                String existingVersion = existingConfig.getString("config-version", "1.0.0");
                String defaultVersion = defaultConfig.getString("config-version", "1.0.0");
                github.mukulx.rtpzonex.utils.Debug.log("CONFIG", fileName + " versions - Existing: " + existingVersion + ", Default: " + defaultVersion);
                if (existingVersion.equals(defaultVersion)) {
                    github.mukulx.rtpzonex.utils.Debug.log("CONFIG", fileName + " is up to date");
                    return true;
                }
                createBackup(configFile);
                int addedKeys = mergeConfigs(existingConfig, defaultConfig);
                if (addedKeys > 0) {
                    existingConfig.set("config-version", defaultVersion);
                    existingConfig.save(configFile);
                    github.mukulx.rtpzonex.utils.Debug.log("CONFIG", "Updated " + fileName + " - Added " + addedKeys + " keys");
                    plugin.getLogger().info("Updated " + fileName + " (v" + existingVersion + " Ã¢â€ â€™ v" + defaultVersion + ") - Added " + addedKeys + " new option(s)");
                    return true;
                }
            } else {
                int addedKeys = mergeConfigs(existingConfig, defaultConfig);
                github.mukulx.rtpzonex.utils.Debug.log("CONFIG", fileName + " - Found " + addedKeys + " missing keys");
                if (addedKeys > 0) {
                    createBackup(configFile);
                    existingConfig.save(configFile);
                    github.mukulx.rtpzonex.utils.Debug.log("CONFIG", "Updated " + fileName + " - Added " + addedKeys + " keys");
                    plugin.getLogger().info("Updated " + fileName + " - Added " + addedKeys + " new option(s)");
                    return true;
                }
            }
        } catch (Exception e) {
            github.mukulx.rtpzonex.utils.Debug.error("CONFIG", "Failed to update " + fileName, e);
            plugin.getLogger().severe("Failed to update " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return false;
    }
    private int mergeConfigs(FileConfiguration existing, FileConfiguration defaults) {
        int addedCount = 0;
        for (String key : defaults.getKeys(true)) {
            if (!existing.contains(key)) {
                Object value = defaults.get(key);
                if (!(value instanceof ConfigurationSection)) {
                    existing.set(key, value);
                    addedCount++;
                }
            }
        }
        if (defaults.contains("config-version")) {
            existing.setComments("config-version", defaults.getComments("config-version"));
        }
        return addedCount;
    }
    private void createBackup(File configFile) throws IOException {
        File backupDir = new File(plugin.getDataFolder(), "backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        String timestamp = dateFormat.format(new Date());
        String backupName = configFile.getName().replace(".yml", "") + "_" + timestamp + ".yml";
        File backupFile = new File(backupDir, backupName);
        Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    public void updateAllConfigs() {
        boolean anyUpdated = false;
        anyUpdated |= updateConfig("config.yml", true);
        anyUpdated |= updateConfig("messages.yml", false);
        anyUpdated |= updateConfig("gui.yml", false);
        if (anyUpdated) {
            plugin.getLogger().info("Configuration files updated successfully!");
        }
    }
    public void cleanupOldBackups(int keepCount) {
        File backupDir = new File(plugin.getDataFolder(), "backups");
        if (!backupDir.exists()) {
            return;
        }

        File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (backups == null || backups.length <= keepCount) {
            return;
        }

        java.util.Arrays.sort(backups, (f1, f2) ->
                Long.compare(f1.lastModified(), f2.lastModified()));

        int toDelete = backups.length - keepCount;
        for (int i = 0; i < toDelete; i++) {
            backups[i].delete();
        }
    }
}
