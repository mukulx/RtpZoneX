# RtpZoneX Setup Guide

## Installation

1. Download the latest release from [Releases](https://github.com/mukulx/RtpZoneX/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure zones using commands or GUI

## First Time Setup

### Method 1: Using the Zone Wand

1. Get the zone wand:
   ```
   /rtpzone wand
   ```

2. Select your zone area:
   - **Left-click** a block to set position 1
   - **Right-click** a block to set position 2
   - Particle effects will show your selection

3. Create the zone:
   ```
   /rtpzone create <zone-name>
   ```

4. Configure the zone:
   ```
   /rtpzone config <zone-name>
   ```
   Or use the GUI: `/rtpzoneadmin gui`

### Method 2: Using the Admin GUI

1. Open the admin GUI:
   ```
   /rtpzoneadmin gui
   ```

2. Click on a zone to configure it

3. Adjust settings using the interactive menu

## Zone Configuration

Each zone has the following options:

### Basic Settings
- **Target World**: The world players will teleport to
- **Min Distance**: Minimum distance from spawn
- **Max Distance**: Maximum distance from spawn
- **Teleport Countdown**: Seconds before teleport
- **Cooldown**: Seconds between teleports

### Player Settings
- **Min Players**: Minimum players required to start
- **Max Players**: Maximum players allowed in zone
- **Wait for Players**: Enable waiting phase
- **Wait Time**: Seconds to wait for more players

### Visual Effects
- **Darkness**: Apply darkness effect during teleport
- **Sound**: Enable teleport sound
- **Particles**: Enable teleport particles
- **Zone Particles**: Show zone boundary particles
- **Titles**: Show countdown titles
- **Show Coordinates**: Display teleport coordinates

### Safety Settings
- **Check Lava**: Avoid lava locations
- **Check Water**: Avoid deep water
- **Min Y**: Minimum Y level
- **Max Y**: Maximum Y level

## Server Compatibility

### Paper Servers
RtpZoneX works on Paper servers without special configuration.

### Folia Servers
RtpZoneX is compatible with Folia's region-based threading:
- Tasks use region-aware scheduling
- Entity operations are thread-safe
- Chunk loading is async
- No global state modifications

## PlaceholderAPI Integration

If you have PlaceholderAPI installed, RtpZoneX provides these placeholders:

- `%rtpzonex_teleports%` - Total teleports by player
- `%rtpzonex_cooldown%` - Remaining cooldown time
- `%rtpzonex_zone%` - Current zone name

## Performance Tips

1. Adjust particle density if you experience lag
2. Increase max attempts for better location finding
3. Use reasonable distance ranges to reduce chunk loading
4. Disable zone particles on large zones if needed
5. Set appropriate Y-level ranges to speed up location finding

## Troubleshooting

### Players not teleporting
- Check if they have the `rtpzonex.use` permission
- Verify the zone boundaries are correct
- Check if cooldown is active
- Ensure target world exists

### No safe location found
- Increase `max-attempts` in zone config
- Adjust Y-level range
- Check if target area has safe blocks
- Disable water/lava checks if needed

### Particles not showing
- Verify particles are enabled in zone config
- Check if players have particles enabled in client
- Try a different particle type
- Reduce particle count if lagging

### Holograms not appearing
- Check if hologram is enabled in zones.yml
- Verify the zone has valid coordinates
- Try reloading the plugin
- Check for conflicts with other hologram plugins

## Advanced Configuration

### Custom Messages
Edit `messages.yml` to customize all plugin messages.

### GUI Customization
Edit `gui.yml` to customize GUI items and layouts.

### Zone Configuration File
Direct editing of `zones.yml` is possible but not recommended. Use the GUI or commands instead.

## Support

If you need help:
1. Check this documentation
2. Look at [existing issues](https://github.com/mukulx/RtpZoneX/issues)
3. Create a new issue with details about your problem
