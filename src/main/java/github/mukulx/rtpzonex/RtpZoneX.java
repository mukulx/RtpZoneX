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
package github.mukulx.rtpzonex;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import github.mukulx.rtpzonex.commands.RtpZoneCommand;
import github.mukulx.rtpzonex.commands.RtpZoneAdminCommand;
import github.mukulx.rtpzonex.listeners.ChatInputListener;
import github.mukulx.rtpzonex.listeners.GUIListener;
import github.mukulx.rtpzonex.listeners.PlayerListener;
import github.mukulx.rtpzonex.listeners.ZoneWandListener;
import github.mukulx.rtpzonex.managers.ConfigManager;
import github.mukulx.rtpzonex.managers.CooldownManager;
import github.mukulx.rtpzonex.managers.HologramManager;
import github.mukulx.rtpzonex.managers.ZoneManager;
import github.mukulx.rtpzonex.placeholders.RtpZoneXPlaceholders;
import github.mukulx.rtpzonex.teleport.TeleportManager;
import github.mukulx.rtpzonex.zone.RtpZone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class RtpZoneX extends JavaPlugin {

    private static RtpZoneX instance;
    private ConfigManager configManager;
    private ZoneManager zoneManager;
    private TeleportManager teleportManager;
    private CooldownManager cooldownManager;
    private HologramManager hologramManager;
    private GUIListener guiListener;
    private ChatInputListener chatInputListener;
    private RtpZoneXPlaceholders placeholders;
    private PlayerListener playerListener;
    private ZoneWandListener zoneWandListener;

    @Override
    public void onEnable() {
        instance = this;
        
        try {
            loadLibraries();
        } catch (Exception e) {
            getComponentLogger().error(Component.text("Failed to load runtime dependencies!", NamedTextColor.RED));
            getComponentLogger().error(Component.text("Error: " + e.getMessage(), NamedTextColor.RED));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        printStartupBanner();
        
        this.configManager = new ConfigManager(this);
        configManager.loadConfig();

        github.mukulx.rtpzonex.utils.Debug.init(this);
        
        this.zoneManager = new ZoneManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.teleportManager = new TeleportManager(this);
        this.hologramManager = new HologramManager(this);
        
        zoneManager.loadZones();
        
        for (RtpZone zone : zoneManager.getAllZones()) {
            hologramManager.createHologram(zone);
        }
        
        hologramManager.startHologramUpdates();
        startZoneParticleTask();
        
        if (!registerCommands()) {
            getComponentLogger().error(Component.text("Failed to register commands! Check plugin.yml", NamedTextColor.RED));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        this.guiListener = new GUIListener(this);
        this.chatInputListener = new ChatInputListener(this);
        this.playerListener = new PlayerListener(this);
        this.zoneWandListener = new ZoneWandListener(this);
        
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(zoneWandListener, this);
        getServer().getPluginManager().registerEvents(guiListener, this);
        getServer().getPluginManager().registerEvents(chatInputListener, this);
        
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholders = new RtpZoneXPlaceholders(this);
            placeholders.register();
            getComponentLogger().info(Component.text("PlaceholderAPI hook enabled!", NamedTextColor.AQUA));
        }
    }
    
    private void loadLibraries() {
        try {
            BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);
            getComponentLogger().info(Component.text("Library loader initialized", NamedTextColor.GREEN));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize library loader", e);
        }
    }
    
    private boolean registerCommands() {
        try {
            var rtpzoneCommand = getCommand("rtpzone");
            var rtpzoneadminCommand = getCommand("rtpzoneadmin");
            
            if (rtpzoneCommand == null) {
                getComponentLogger().error(Component.text("Command 'rtpzone' not found in plugin.yml", NamedTextColor.RED));
                return false;
            }
            
            if (rtpzoneadminCommand == null) {
                getComponentLogger().error(Component.text("Command 'rtpzoneadmin' not found in plugin.yml", NamedTextColor.RED));
                return false;
            }
            
            rtpzoneCommand.setExecutor(new RtpZoneCommand(this));
            rtpzoneadminCommand.setExecutor(new RtpZoneAdminCommand(this));
            
            return true;
        } catch (Exception e) {
            getComponentLogger().error(Component.text("Error registering commands: " + e.getMessage(), NamedTextColor.RED));
            return false;
        }
    }
    
    private void startZoneParticleTask() {

        if (!github.mukulx.rtpzonex.utils.SchedulerUtil.isFolia()) {
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskTimer(this, () -> {
                for (RtpZone zone : zoneManager.getAllZones()) {
                    if (zone.getConfig().isZoneParticlesEnabled()) {
                        zone.spawnBoundaryParticles(zone.getConfig().getParticleType(), 2);
                    }
                }
            }, 20L, 20L);
        }
    }
    
    private void printStartupBanner() {
        getComponentLogger().info(Component.text("  ██████╗ ████████╗██████╗ ███████╗ ██████╗ ███╗   ██╗███████╗██╗  ██╗", NamedTextColor.AQUA));
        getComponentLogger().info(Component.text("  ██╔══██╗╚══██╔══╝██╔══██╗╚══███╔╝██╔═══██╗████╗  ██║██╔════╝╚██╗██╔╝", NamedTextColor.AQUA));
        getComponentLogger().info(Component.text("  ██████╔╝   ██║   ██████╔╝  ███╔╝ ██║   ██║██╔██╗ ██║█████╗   ╚███╔╝ ", NamedTextColor.AQUA));
        getComponentLogger().info(Component.text("  ██╔══██╗   ██║   ██╔═══╝  ███╔╝  ██║   ██║██║╚██╗██║██╔══╝   ██╔██╗ ", NamedTextColor.AQUA));
        getComponentLogger().info(Component.text("  ██║  ██║   ██║   ██║     ███████╗╚██████╔╝██║ ╚████║███████╗██╔╝ ██╗", NamedTextColor.AQUA));
        getComponentLogger().info(Component.text("  ╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚══════╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝", NamedTextColor.AQUA));
        getComponentLogger().info(Component.text("                    v" + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0) + " :)", NamedTextColor.AQUA));
    }

    @Override
    public void onDisable() {
        if (hologramManager != null) {
            hologramManager.stopHologramUpdates();
            hologramManager.removeAllHolograms();
        }
        
        if (teleportManager != null) {
            teleportManager.cancelAllTeleports();
        }
        
        if (zoneManager != null) {
            zoneManager.saveZones();
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) {
                String title = player.getOpenInventory().getTitle();
                if (title.contains("RTP") || title.contains("Zone") || title.contains("Select")) {
                    player.closeInventory();
                }
            }
        }
        
        if (placeholders != null) {
            placeholders.unregister();
            placeholders = null;
        }
        
        HandlerList.unregisterAll(this);
        github.mukulx.rtpzonex.utils.SchedulerUtil.cancelAllTasks(this);
        
        instance = null;
        configManager = null;
        zoneManager = null;
        teleportManager = null;
        cooldownManager = null;
        hologramManager = null;
        guiListener = null;
        chatInputListener = null;
        playerListener = null;
        zoneWandListener = null;
        
        getLogger().info("RtpZoneX has been disabled!");
    }

    public static RtpZoneX getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    public HologramManager getHologramManager() {
        return hologramManager;
    }
    
    public GUIListener getGUIListener() {
        return guiListener;
    }
    
    public ChatInputListener getChatInputListener() {
        return chatInputListener;
    }

    public void reload() {
        getLogger().info("Reloading RtpZoneX...");

        if (teleportManager != null) {
            teleportManager.cancelAllTeleports();
        }

        if (hologramManager != null) {
            hologramManager.stopHologramUpdates();
            hologramManager.removeAllHolograms();
        }

        configManager.loadConfig();

        zoneManager.loadZones();

        for (RtpZone zone : zoneManager.getAllZones()) {
            hologramManager.createHologram(zone);
        }

        hologramManager.startHologramUpdates();
        
        getLogger().info("RtpZoneX reloaded successfully!");
    }
}
