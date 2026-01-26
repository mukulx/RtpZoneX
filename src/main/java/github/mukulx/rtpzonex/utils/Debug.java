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
package github.mukulx.rtpzonex.utils;

import github.mukulx.rtpzonex.RtpZoneX;

public class Debug {

    private static RtpZoneX plugin;
    private static boolean enabled = false;

    public static void init(RtpZoneX plugin) {
        Debug.plugin = plugin;
        Debug.enabled = plugin.getConfig().getBoolean("debug.enabled", false);
    }

    public static void setEnabled(boolean enabled) {
        Debug.enabled = enabled;
        if (plugin != null) {
            plugin.getConfig().set("debug.enabled", enabled);
            plugin.saveConfig();
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void log(String message) {
        if (enabled && plugin != null) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public static void log(String category, String message) {
        if (enabled && plugin != null) {
            plugin.getLogger().info("[DEBUG:" + category + "] " + message);
        }
    }

    public static void warn(String message) {
        if (enabled && plugin != null) {
            plugin.getLogger().warning("[DEBUG] " + message);
        }
    }

    public static void warn(String category, String message) {
        if (enabled && plugin != null) {
            plugin.getLogger().warning("[DEBUG:" + category + "] " + message);
        }
    }

    public static void error(String message) {
        if (enabled && plugin != null) {
            plugin.getLogger().severe("[DEBUG] " + message);
        }
    }

    public static void error(String category, String message) {
        if (enabled && plugin != null) {
            plugin.getLogger().severe("[DEBUG:" + category + "] " + message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if (enabled && plugin != null) {
            plugin.getLogger().severe("[DEBUG] " + message);
            throwable.printStackTrace();
        }
    }

    public static void error(String category, String message, Throwable throwable) {
        if (enabled && plugin != null) {
            plugin.getLogger().severe("[DEBUG:" + category + "] " + message);
            throwable.printStackTrace();
        }
    }
}
