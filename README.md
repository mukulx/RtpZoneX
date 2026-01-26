<div align="center">

# RtpZoneX

**Advanced zone-based random teleportation for Minecraft**

[![Build](https://img.shields.io/github/actions/workflow/status/mukulx/RtpZoneX/build.yml?branch=main)](https://github.com/mukulx/RtpZoneX/actions)
[![Folia](https://img.shields.io/badge/Folia-Compatible-green)](https://papermc.io/software/folia)
[![Paper](https://img.shields.io/badge/Paper-1.21+-blue)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-GPL--3.0-red)](LICENSE)

</div>

---

## Overview

RtpZoneX is a Minecraft plugin for zone-based random teleportation with group support. Players entering zones are teleported together to safe random locations. Works great for survival servers, lifesteal servers, minigames, and adventure maps.

## Features

- Zone-based RTP with custom boundaries
- Group teleportation system
- Zone wand for easy setup
- Safe location finder (avoids lava, water, unsafe blocks)
- Particle effects and holograms
- Customizable sounds and visual effects
- Interactive GUI for configuration
- Multi-world support
- Cooldown system
- Full Folia and Paper compatibility

## Installation

1. Download the latest release from [Releases](../../releases)
2. Place `RtpZoneX-1.0.0.jar` in your `plugins` folder
3. Restart your server
4. Use `/rtpzone wand` to create zones

**Requirements:** Paper 1.21.4+ or Folia, Java 21+

## Quick Start

```bash
# Get the zone wand
/rtpzone wand

# Left-click and right-click blocks to set positions
# Create the zone
/rtpzone create spawn-rtp

# Configure it
/rtpzone config spawn-rtp
```

See [Quick Start Guide](docs/QUICKSTART.md) for detailed instructions.

## Documentation

- [Quick Start Guide](docs/QUICKSTART.md)
- [Setup Guide](docs/SETUP.md)
- [API Documentation](docs/API.md)

## Contributing

Contributions are welcome! Read [Contributing Guidelines](CONTRIBUTING.md) before submitting pull requests.

## Support

- [Bug Reports & Feature Requests](../../issues)

## License

Licensed under GNU General Public License v3.0. See [LICENSE](LICENSE) for details.

---

<div align="center">

**[Download](../../releases)** | **[Documentation](docs/)** | **[Issues](../../issues)**