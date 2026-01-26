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
package github.mukulx.rtpzonex.placeholders;
import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.zone.RtpZone;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class RtpZoneXPlaceholders extends PlaceholderExpansion {
    private final RtpZoneX plugin;
    public RtpZoneXPlaceholders(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "RtpZoneX";
    }
    @Override
    public @NotNull String getAuthor() {
        return "mukulx";
    }
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        switch (params.toLowerCase()) {
            case "cooldown":
            return String.valueOf(plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId()));
            case "zones":
            return String.valueOf(plugin.getZoneManager().getZoneCount());
            case "in_zone":
            RtpZone zone = plugin.getZoneManager().getZoneAt(player.getLocation());
            return zone != null ? "true" : "false";
            case "zone_name":
            RtpZone currentZone = plugin.getZoneManager().getZoneAt(player.getLocation());
            return currentZone != null ? currentZone.getName() : "None";
            case "total_teleports":
            return String.valueOf(plugin.getCooldownManager().getTeleportCount(player.getUniqueId()));
            case "is_teleporting":
            return plugin.getTeleportManager().isPlayerTeleporting(player.getUniqueId()) ? "true" : "false";
            case "teleport_zone":
            String teleportZone = plugin.getTeleportManager().getPlayerTeleportZone(player.getUniqueId());
            return teleportZone != null ? teleportZone : "None";
            case "max_distance":
            return String.valueOf(plugin.getConfigManager().getMaxDistance());
            case "min_distance":
            return String.valueOf(plugin.getConfigManager().getMinDistance());
            case "countdown":
            return String.valueOf(plugin.getConfigManager().getTeleportCountdown());
            default:
            return null;
        }
    }
}
