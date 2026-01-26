# Quick Start Guide

Get RtpZoneX running in 5 minutes.

## Installation

1. Download `RtpZoneX-1.0.0.jar` from [Releases](https://github.com/mukulx/RtpZoneX/releases)
2. Place it in your `plugins` folder
3. Restart your server

## Create Your First Zone

### Using the Wand

```
/rtpzone wand
```

Left-click a block for position 1, right-click for position 2.

```
/rtpzone create spawn-rtp
/rtpzone config spawn-rtp
```

### Using the GUI

```
/rtpzoneadmin gui
```

Click on a zone to configure it.

## Configure Your Zone

In the configuration GUI:

- **Target World** - Where players teleport to
- **Distance** - Min 100, Max 5000 blocks from spawn
- **Players** - Min 1, Max 10 players
- **Countdown** - 5 seconds before teleport
- **Cooldown** - 60 seconds between uses

Click "Save & Close" when done.

## Test It

Walk into your zone to trigger the teleportation.

## Common Configurations

**Solo RTP Zone**
```
Min Players: 1
Max Players: 1
Wait for Players: OFF
Countdown: 3 seconds
```

**Group RTP Zone**
```
Min Players: 2
Max Players: 10
Wait for Players: ON
Wait Time: 15 seconds
Countdown: 5 seconds
```

**Instant RTP Zone**
```
Min Players: 1
Wait for Players: OFF
Countdown: 0 seconds
```

## Useful Commands

- `/rtpzoneadmin zones` - List all zones
- `/rtpzoneadmin info` - Show plugin stats
- `/rtpzoneadmin debug` - Toggle debug mode

## Next Steps

- [Setup Guide](SETUP.md) - Detailed configuration
- [API Documentation](API.md) - For developers

## Need Help?

- [Existing Issues](https://github.com/mukulx/RtpZoneX/issues)
- [Create New Issue](https://github.com/mukulx/RtpZoneX/issues/new)
