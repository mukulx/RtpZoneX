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
import github.mukulx.rtpzonex.zone.RtpZone;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {
    private final RtpZoneX plugin;
    public PlayerListener(RtpZoneX plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
        && event.getFrom().getBlockY() == event.getTo().getBlockY()
        && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("rtpzonex.use")) {
            return;
        }
        RtpZone fromZone = plugin.getZoneManager().getZoneAt(event.getFrom());
        RtpZone toZone = plugin.getZoneManager().getZoneAt(event.getTo());
        if (fromZone != null && toZone == null) {
            plugin.getTeleportManager().playerLeftZone(fromZone, player);
        }
        else if (fromZone == null && toZone != null) {
            plugin.getTeleportManager().playerEnteredZone(toZone, player);
        }
        else if (fromZone != null && toZone != null && !fromZone.getName().equals(toZone.getName())) {
            plugin.getTeleportManager().playerLeftZone(fromZone, player);
            plugin.getTeleportManager().playerEnteredZone(toZone, player);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (RtpZone zone : plugin.getZoneManager().getAllZones()) {
            if (zone.hasPlayer(player.getUniqueId())) {
                plugin.getTeleportManager().playerLeftZone(zone, player);
            }
        }
    }
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
            Player player = event.getPlayer();
            RtpZone fromZone = plugin.getZoneManager().getZoneAt(event.getFrom());
            if (fromZone != null && fromZone.hasPlayer(player.getUniqueId())) {
                RtpZone toZone = plugin.getZoneManager().getZoneAt(event.getTo());
                if (toZone == null || !toZone.getName().equals(fromZone.getName())) {
                    plugin.getTeleportManager().playerLeftZone(fromZone, player);
                }
            }
        }
    }
}
