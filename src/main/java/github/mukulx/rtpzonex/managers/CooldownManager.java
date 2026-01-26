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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final RtpZoneX plugin;
    private final Map<UUID, Long> cooldowns;
    private final Map<UUID, Integer> teleportCounts;
    public CooldownManager(RtpZoneX plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
        this.teleportCounts = new HashMap<>();
    }
    public boolean isOnCooldown(UUID playerId) {
        return isOnCooldown(playerId, plugin.getConfigManager().getCooldown());
    }
    public boolean isOnCooldown(UUID playerId, int cooldownSeconds) {
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }

        long lastTeleport = cooldowns.get(playerId);
        long cooldownEnd = lastTeleport + (cooldownSeconds * 1000L);

        return System.currentTimeMillis() < cooldownEnd;
    }
    public int getRemainingCooldown(UUID playerId) {
        return getRemainingCooldown(playerId, plugin.getConfigManager().getCooldown());
    }
    public int getRemainingCooldown(UUID playerId, int cooldownSeconds) {
        if (!cooldowns.containsKey(playerId)) {
            return 0;
        }

        long lastTeleport = cooldowns.get(playerId);
        long cooldownEnd = lastTeleport + (cooldownSeconds * 1000L);
        long remaining = cooldownEnd - System.currentTimeMillis();

        if (remaining <= 0) {
            return 0;
        }

        return (int) (remaining / 1000);
    }
    public void setCooldown(UUID playerId) {
        cooldowns.put(playerId, System.currentTimeMillis());
    }
    public void removeCooldown(UUID playerId) {
        cooldowns.remove(playerId);
    }
    public void incrementTeleportCount(UUID playerId) {
        teleportCounts.merge(playerId, 1, Integer::sum);
    }
    public int getTeleportCount(UUID playerId) {
        return teleportCounts.getOrDefault(playerId, 0);
    }
    public void clearAllCooldowns() {
        cooldowns.clear();
    }
}
