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
package github.mukulx.rtpzonex.zone;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RtpZone {

    private final String name;
    private final Location pos1;
    private final Location pos2;
    private final Set<UUID> playersInZone;
    private final ZoneConfig config;

    private boolean teleportInProgress;
    private boolean waitingForPlayers;
    private int waitTimeRemaining;
    private Location pendingTeleportLocation;

    public RtpZone(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.playersInZone = new HashSet<>();
        this.config = new ZoneConfig();
        this.teleportInProgress = false;
        this.waitingForPlayers = false;
        this.waitTimeRemaining = 0;
        this.pendingTeleportLocation = null;
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public World getWorld() {
        return pos1.getWorld();
    }

    public ZoneConfig getConfig() {
        return config;
    }

    public boolean contains(Location location) {
        if (location.getWorld() == null || !location.getWorld().equals(pos1.getWorld())) {
            return false;
        }

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    public void addPlayer(UUID playerId) {
        playersInZone.add(playerId);
    }

    public void removePlayer(UUID playerId) {
        playersInZone.remove(playerId);
    }

    public boolean hasPlayer(UUID playerId) {
        return playersInZone.contains(playerId);
    }

    public Set<UUID> getPlayersInZone() {
        return new HashSet<>(playersInZone);
    }

    public int getPlayerCount() {
        return playersInZone.size();
    }

    public void clearPlayers() {
        playersInZone.clear();
    }

    public boolean isTeleportInProgress() {
        return teleportInProgress;
    }

    public void setTeleportInProgress(boolean inProgress) {
        this.teleportInProgress = inProgress;
    }

    public boolean isWaitingForPlayers() {
        return waitingForPlayers;
    }

    public void setWaitingForPlayers(boolean waiting) {
        this.waitingForPlayers = waiting;
    }

    public int getWaitTimeRemaining() {
        return waitTimeRemaining;
    }

    public void setWaitTimeRemaining(int time) {
        this.waitTimeRemaining = time;
    }

    public void decrementWaitTime() {
        if (waitTimeRemaining > 0) {
            waitTimeRemaining--;
        }
    }

    public Location getPendingTeleportLocation() {
        return pendingTeleportLocation;
    }

    public void setPendingTeleportLocation(Location location) {
        this.pendingTeleportLocation = location;
    }

    public void resetTeleportState() {
        this.teleportInProgress = false;
        this.waitingForPlayers = false;
        this.waitTimeRemaining = 0;
        this.pendingTeleportLocation = null;
    }

    public void spawnBoundaryParticles(org.bukkit.Particle particle, int density) {
        if (!config.isZoneParticlesEnabled()) {
            return;
        }

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        World world = pos1.getWorld();
        if (world == null) {
            return;
        }

        // Bottom edges
        spawnEdgeParticles(world, particle, minX, minY, minZ, maxX, minY, minZ, density);
        spawnEdgeParticles(world, particle, minX, minY, maxZ, maxX, minY, maxZ, density);
        spawnEdgeParticles(world, particle, minX, minY, minZ, minX, minY, maxZ, density);
        spawnEdgeParticles(world, particle, maxX, minY, minZ, maxX, minY, maxZ, density);

        // Top edges
        spawnEdgeParticles(world, particle, minX, maxY, minZ, maxX, maxY, minZ, density);
        spawnEdgeParticles(world, particle, minX, maxY, maxZ, maxX, maxY, maxZ, density);
        spawnEdgeParticles(world, particle, minX, maxY, minZ, minX, maxY, maxZ, density);
        spawnEdgeParticles(world, particle, maxX, maxY, minZ, maxX, maxY, maxZ, density);

        // Vertical edges
        spawnEdgeParticles(world, particle, minX, minY, minZ, minX, maxY, minZ, density);
        spawnEdgeParticles(world, particle, maxX, minY, minZ, maxX, maxY, minZ, density);
        spawnEdgeParticles(world, particle, minX, minY, maxZ, minX, maxY, maxZ, density);
        spawnEdgeParticles(world, particle, maxX, minY, maxZ, maxX, maxY, maxZ, density);
    }

    private void spawnEdgeParticles(World world, org.bukkit.Particle particle,
                                     int x1, int y1, int z1, int x2, int y2, int z2, int density) {
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
        int steps = Math.max(1, (int) (distance / density));

        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            double x = x1 + (x2 - x1) * ratio;
            double y = y1 + (y2 - y1) * ratio;
            double z = z1 + (z2 - z1) * ratio;

            Location loc = new Location(world, x + 0.5, y + 0.5, z + 0.5);
            spawnParticleSafe(world, particle, loc);
        }
    }

    private void spawnParticleSafe(World world, org.bukkit.Particle particle, Location location) {
        try {
            switch (particle) {
                case DUST:
                    world.spawnParticle(particle, location, 1, 0, 0, 0, 0,
                            new org.bukkit.Particle.DustOptions(org.bukkit.Color.PURPLE, 1.0f));
                    break;
                case DUST_COLOR_TRANSITION:
                    world.spawnParticle(particle, location, 1, 0, 0, 0, 0,
                            new org.bukkit.Particle.DustTransition(org.bukkit.Color.PURPLE, org.bukkit.Color.BLUE, 1.0f));
                    break;
                case BLOCK:
                case FALLING_DUST:
                case ITEM:
                    world.spawnParticle(org.bukkit.Particle.FLAME, location, 1, 0, 0, 0, 0);
                    break;
                default:
                    world.spawnParticle(particle, location, 1, 0, 0, 0, 0);
                    break;
            }
        } catch (Exception e) {
            try {
                world.spawnParticle(org.bukkit.Particle.FLAME, location, 1, 0, 0, 0, 0);
            } catch (Exception ignored) {
                // Silently fail if particle spawning is not supported
            }
        }
    }
}