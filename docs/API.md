# RtpZoneX API Documentation

## Using RtpZoneX in Your Plugin

### Adding RtpZoneX as a Dependency

#### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.mukulx:RtpZoneX:1.0.0")
}
```

#### Gradle (Groovy)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.mukulx:RtpZoneX:1.0.0'
}
```

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.mukulx</groupId>
        <artifactId>RtpZoneX</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Getting the Plugin Instance

```java
import github.mukulx.rtpzonex.RtpZoneX;

RtpZoneX plugin = RtpZoneX.getInstance();
```

## API Examples

### Working with Zones

#### Get a Zone
```java
import github.mukulx.rtpzonex.zone.RtpZone;

RtpZone zone = plugin.getZoneManager().getZone("zoneName");
```

#### Get Zone at Location
```java
import org.bukkit.Location;

Location location = player.getLocation();
RtpZone zone = plugin.getZoneManager().getZoneAt(location);
```

#### Get All Zones
```java
import java.util.Collection;

Collection<RtpZone> zones = plugin.getZoneManager().getAllZones();
```

#### Create a Zone
```java
import org.bukkit.Location;

Location pos1 = new Location(world, x1, y1, z1);
Location pos2 = new Location(world, x2, y2, z2);

boolean success = plugin.getZoneManager().createZone("myZone", pos1, pos2);
```

#### Delete a Zone
```java
boolean success = plugin.getZoneManager().deleteZone("zoneName");
```

### Zone Information

#### Check if Player is in Zone
```java
import java.util.UUID;

UUID playerId = player.getUniqueId();
boolean inZone = zone.hasPlayer(playerId);
```

#### Get Players in Zone
```java
import java.util.Set;
import java.util.UUID;

Set<UUID> players = zone.getPlayersInZone();
```

#### Get Zone Status
```java
boolean teleporting = zone.isTeleportInProgress();
boolean waiting = zone.isWaitingForPlayers();
int waitTime = zone.getWaitTimeRemaining();
```

### Zone Configuration

#### Get Zone Config
```java
import github.mukulx.rtpzonex.zone.ZoneConfig;

ZoneConfig config = zone.getConfig();
```

#### Modify Zone Settings
```java
config.setTargetWorld("world_nether");
config.setMinDistance(1000);
config.setMaxDistance(10000);
config.setTeleportCountdown(10);
config.setCooldown(120);

// Save changes
plugin.getZoneManager().saveZones();
```

### Teleportation

#### Start Group Teleport
```java
plugin.getTeleportManager().startGroupTeleport(zone);
```

#### Check if Player is Teleporting
```java
boolean isTeleporting = plugin.getTeleportManager().isPlayerTeleporting(player.getUniqueId());
```

#### Get Player's Teleport Zone
```java
String zoneName = plugin.getTeleportManager().getPlayerTeleportZone(player.getUniqueId());
```

### Cooldown Management

#### Check Cooldown
```java
boolean onCooldown = plugin.getCooldownManager().isOnCooldown(player.getUniqueId());
```

#### Get Remaining Cooldown
```java
int remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId());
```

#### Set Cooldown
```java
plugin.getCooldownManager().setCooldown(player.getUniqueId());
```

#### Remove Cooldown
```java
plugin.getCooldownManager().removeCooldown(player.getUniqueId());
```

### Safe Location Finding

#### Find Safe Location
```java
import github.mukulx.rtpzonex.teleport.SafeLocationFinder;
import org.bukkit.World;
import java.util.concurrent.CompletableFuture;

SafeLocationFinder finder = plugin.getTeleportManager().getLocationFinder();
World world = Bukkit.getWorld("world");
ZoneConfig config = zone.getConfig();

CompletableFuture<Location> future = finder.findSafeLocation(world, config);
future.thenAccept(location -> {
    if (location != null) {
        // Safe location found
        player.teleport(location);
    } else {
        // No safe location found
        player.sendMessage("No safe location found!");
    }
});
```

#### Check if Location is Safe
```java
boolean safe = finder.isSafeLocation(location, config);
```

### Hologram Management

#### Create Hologram
```java
plugin.getHologramManager().createHologram(zone);
```

#### Update Hologram
```java
plugin.getHologramManager().updateHologram(zone);
```

#### Remove Hologram
```java
plugin.getHologramManager().removeHologram(zone.getName());
```

### Configuration

#### Get Config Values
```java
String prefix = plugin.getConfigManager().getPrefix();
String message = plugin.getConfigManager().getMessage("zone-entered");
int countdown = plugin.getConfigManager().getTeleportCountdown();
int cooldown = plugin.getConfigManager().getCooldown();
```

#### Reload Configuration
```java
plugin.reload();
```

## Events

RtpZoneX doesn't provide custom events, but you can listen to standard Bukkit events:

```java
@EventHandler
public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    RtpZone zone = plugin.getZoneManager().getZoneAt(event.getTo());
    
    if (zone != null) {
        // Player is in a zone
    }
}
```

## Thread Safety

RtpZoneX is designed to be thread-safe and Folia-compatible:

- Task scheduling uses `SchedulerUtil` for region handling
- Entity operations run on the correct region thread
- Chunk loading is asynchronous
- Collections are synchronized where needed

When using the API:
- Use `SchedulerUtil` for scheduled tasks
- Execute entity/location operations on the correct thread
- Use async operations for heavy computations

## Best Practices

1. Always check for null when getting zones or locations
2. Use CompletableFuture for async operations
3. Save zones after modifying configurations
4. Handle exceptions when working with locations
5. Test on both Paper and Folia if your plugin supports both

## Support

For API support:
- Open an issue on [GitHub](https://github.com/mukulx/RtpZoneX/issues)
- Check existing documentation
- Review the source code
