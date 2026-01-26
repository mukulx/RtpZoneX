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
package github.mukulx.rtpzonex.teleport;
import github.mukulx.rtpzonex.RtpZoneX;
import github.mukulx.rtpzonex.zone.ZoneConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
public class SafeLocationFinder {
    private final RtpZoneX plugin;
    private final Random random;
    private final Set<Material> unsafeBlocks;
    private final Set<Location> usedLocations;
    public SafeLocationFinder(RtpZoneX plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.unsafeBlocks = new HashSet<>();
        this.usedLocations = new HashSet<>();
        loadUnsafeBlocks();
    }
    private void loadUnsafeBlocks() {
        unsafeBlocks.add(Material.LAVA);
        unsafeBlocks.add(Material.FIRE);
        unsafeBlocks.add(Material.CACTUS);
        unsafeBlocks.add(Material.MAGMA_BLOCK);
        unsafeBlocks.add(Material.CAMPFIRE);
        unsafeBlocks.add(Material.SOUL_CAMPFIRE);
        unsafeBlocks.add(Material.SWEET_BERRY_BUSH);
        unsafeBlocks.add(Material.WITHER_ROSE);
        unsafeBlocks.add(Material.POWDER_SNOW);
    }
    public CompletableFuture<Location> findSafeLocation(World world, ZoneConfig config) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        tryFindLocation(world, config, 0, config.getMaxAttempts(), future);
        return future;
    }
    private void tryFindLocation(World world, ZoneConfig config, int attempt, int maxAttempts,
    CompletableFuture<Location> future) {
        if (attempt >= maxAttempts) {
            future.complete(null);
            return;
        }
        Location spawnLocation = world.getSpawnLocation();
        double angle = random.nextDouble() * 2 * Math.PI;
        int distance = config.getMinDistance() + random.nextInt(Math.max(1, config.getMaxDistance() - config.getMinDistance()));
        int x = (int) (spawnLocation.getX() + distance * Math.cos(angle));
        int z = (int) (spawnLocation.getZ() + distance * Math.sin(angle));
        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            github.mukulx.rtpzonex.utils.SchedulerUtil.runTaskAtLocation(plugin,
            new Location(world, x, 64, z), () -> {
                try {
                    Location potentialLocation = findSafeY(world, x, z, config.getMinY(), config.getMaxY(), config);
                    if (potentialLocation != null && !isLocationUsed(potentialLocation)) {
                        future.complete(potentialLocation);
                    } else {
                        tryFindLocation(world, config, attempt + 1, maxAttempts, future);
                    }
                } catch (Exception e) {
                    tryFindLocation(world, config, attempt + 1, maxAttempts, future);
                }
            });
        });
    }
    private Location findSafeLocationSync(World world, int x, int z, int minY, int maxY,
    ZoneConfig config, int maxAttempts, int attempt) {
        if (attempt >= maxAttempts) {
            return null;
        }
        Location potentialLocation = findSafeY(world, x, z, minY, maxY, config);
        if (potentialLocation != null && !isLocationUsed(potentialLocation)) {
            return potentialLocation;
        }
        Location spawnLocation = world.getSpawnLocation();
        double angle = random.nextDouble() * 2 * Math.PI;
        int distance = config.getMinDistance() + random.nextInt(Math.max(1, config.getMaxDistance() - config.getMinDistance()));
        int newX = (int) (spawnLocation.getX() + distance * Math.cos(angle));
        int newZ = (int) (spawnLocation.getZ() + distance * Math.sin(angle));
        return findSafeLocationSync(world, newX, newZ, minY, maxY, config, maxAttempts, attempt + 1);
    }
    private Location findSafeY(World world, int x, int z, int minY, int maxY, ZoneConfig config) {
        if (world.getEnvironment() == World.Environment.NETHER) {
            return findNetherSafeY(world, x, z, minY, Math.min(maxY, 120), config);
        }
        if (world.getEnvironment() == World.Environment.THE_END) {
            return findEndSafeY(world, x, z, config);
        }
        int highestY = world.getHighestBlockYAt(x, z);
        if (highestY < minY || highestY > maxY) {
            return null;
        }
        Location location = new Location(world, x + 0.5, highestY + 1, z + 0.5);
        if (isSafeLocation(location, config)) {
            return location;
        }
        return null;
    }
    private Location findNetherSafeY(World world, int x, int z, int minY, int maxY, ZoneConfig config) {
        for (int y = minY; y < maxY; y++) {
            Location location = new Location(world, x + 0.5, y, z + 0.5);
            if (isSafeLocation(location, config)) {
                return location;
            }
        }
        return null;
    }
    private Location findEndSafeY(World world, int x, int z, ZoneConfig config) {
        int highestY = world.getHighestBlockYAt(x, z);
        if (highestY <= 0) {
            return null;
        }
        Location location = new Location(world, x + 0.5, highestY + 1, z + 0.5);
        if (isSafeLocation(location, config)) {
            return location;
        }
        return null;
    }
    public boolean isSafeLocation(Location location, ZoneConfig config) {
        Block feetBlock = location.getBlock();
        Block headBlock = feetBlock.getRelative(0, 1, 0);
        Block groundBlock = feetBlock.getRelative(0, -1, 0);
        if (!feetBlock.isPassable() || !headBlock.isPassable()) {
            return false;
        }
        if (!groundBlock.getType().isSolid()) {
            return false;
        }
        if (unsafeBlocks.contains(groundBlock.getType())) {
            return false;
        }
        if (config.isCheckLava()) {
            if (hasNearbyBlock(location, Material.LAVA, 2)) {
                return false;
            }
        }
        if (config.isCheckWater()) {
            if (isDeepWater(location)) {
                return false;
            }
        }
        return true;
    }
    private boolean hasNearbyBlock(Location location, Material material, int radius) {
        World world = location.getWorld();
        int baseX = location.getBlockX();
        int baseY = location.getBlockY();
        int baseZ = location.getBlockZ();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (world.getBlockAt(baseX + x, baseY + y, baseZ + z).getType() == material) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isDeepWater(Location location) {
        Block block = location.getBlock().getRelative(0, -1, 0);
        int waterDepth = 0;
        while (block.getType() == Material.WATER && waterDepth < 5) {
            waterDepth++;
            block = block.getRelative(0, -1, 0);
        }
        return waterDepth >= 3;
    }
    public void markLocationUsed(Location location) {
        Location rounded = new Location(
        location.getWorld(),
        Math.floor(location.getX() / 16) * 16,
        location.getY(),
        Math.floor(location.getZ() / 16) * 16
        );
        usedLocations.add(rounded);
    }
    public boolean isLocationUsed(Location location) {
        Location rounded = new Location(
        location.getWorld(),
        Math.floor(location.getX() / 16) * 16,
        location.getY(),
        Math.floor(location.getZ() / 16) * 16
        );
        return usedLocations.contains(rounded);
    }
    public void clearUsedLocations() {
        usedLocations.clear();
    }
}
