# LovelyEasyPlace

**LovelyEasyPlace** is a premium, lightweight Fabric client mod for **Minecraft 1.21.11** designed to streamline technical redstone construction and schematic building. It eliminates tedious manual sneaking when placing blocks against interactive containers/workstations, while seamlessly integrating with **Litematica** schematics for automatic block rotation, note block pitch tuning, and redstone state matching.

> [!IMPORTANT]
> **100% Offline & Account Safe**: LovelyEasyPlace contains **zero external network calls**, zero telemetry, zero update checkers, and zero data logging. All operations execute strictly on your local Minecraft client.

> [!CAUTION]
> **Multiplayer Compliance**: Multiplayer support is disabled by default (`allowMultiplayer=false`). Always check server rules before enabling placement helpers on public servers. Built-in server shields automatically enforce safety on strict competitive networks.

---

## 🛡️ Security & Anticheat Safety (OpSec)

LovelyEasyPlace is engineered from the ground up to protect your account from malicious code vectors and anticheat flags (e.g., GrimAC, Vulcan, Matrix):

- **Zero External Connections**: Pure offline client mod with no HTTP clients, webhooks, or dynamic remote loaders. Your Minecraft access tokens and credentials remain completely isolated and secure.
- **Grand Common Divisor (GCD) Alignment**: Rotation packets sent during schematic auto-rotation are mathematically aligned to Minecraft's mouse sensitivity steps (`alignToGCD`), preventing impossible micro-rotation kicks.
- **Pitch Clamping**: Horizontal rotations clamp pitch between `-45.0°` and `+45.0°` to preserve valid line-of-sight checks and prevent illegal packet angles.
- **Single-Tick Rate Limiting**: Multi-click adjustments (Note Block tuning, Repeater delay, Comparator mode) are executed through a single-action-per-tick queue (`clickQueue`), preventing packet flood kicks.
- **Input Watchdog**: Automatically resets temporary sneak inputs if an interaction fails or is dropped by the client/server.
- **Built-in Server Shields**: Automatically disables on known competitive servers (`hypixel.net`, `mccisland.net`, `cubecraft.net`, `wynncraft.com`, `manacube.com`).

---

## ✨ Key Features

### 1. Easy Container Placement (Fake Sneaking)
Places blocks directly against interactive blocks without opening their GUI window:
- **Chests & Storage**: Chests, Trapped Chests, Ender Chests, Shulker Boxes, Barrels, Chiseled Bookshelves.
- **Redstone & Automation**: Hoppers, Dispensers, Droppers, Crafters.
- **Workstations**: Furnaces, Smokers, Blast Furnaces, Anvils, Crafting Tables, Brewing Stands, Beacons, Enchanting Tables, Lecterns, Jukeboxes, Note Blocks.
- **Utility Blocks**: Looms, Cartography Tables, Grindstones, Stonecutters, Smithing Tables.

### 2. Litematica Integration & Auto-Tuning
When [Litematica](https://modrinth.com/mod/litematica) is loaded with an active schematic:
- **Auto-Rotation**: Aligns directional blocks (Observers, Pistons, Repeaters, Comparators, Dispensers, Stairs, Anvils, Doors) to match the schematic orientation.
- **Note Block Auto-Pitch**: Automatically tunes placed or right-clicked Note Blocks to the exact target pitch required by the schematic.
- **Redstone State Matching**: Automatically sets Repeater delay ticks and Comparator subtraction/comparison modes to match the schematic.

### 3. Smart Placement Controls
- **Reverse Placement Mode**: Places directional blocks facing away from the player when no schematic reference is present.
- **Hold-to-Activate Mode**: Configurable hotkey to only enable placement features while held.
- **HUD Status Overlay**: Displays current mod status (`LEP ON`, `LEP OFF`, or `LEP LOCK`) on your game screen.

---

## 📋 Requirements

| Component | Minimum Version | Note |
| :--- | :--- | :--- |
| **Minecraft** | `1.21.11` | Required |
| **Java** | `21` or newer | Required |
| **Fabric Loader** | `0.19.3` or newer | Required |
| **Fabric API** | `1.21.11` version | Required |
| **Cloth Config** | Bundled | Required for full GUI screen |
| **Litematica** | Optional | Required for schematic auto-rotation & state tuning |
| **Mod Menu** | Optional | Access config screen from mod list |

---

## 🚀 Installation

1. Download and install **Fabric Loader** for Minecraft 1.21.11.
2. Place **Fabric API** into your `.minecraft/mods` directory.
3. Place the **LovelyEasyPlace** `.jar` file into `.minecraft/mods`.
4. *(Optional)* Add **Litematica** for schematic matching and **Mod Menu** for in-game configuration UI.
5. Launch Minecraft using the Fabric profile.

---

## 🎮 Keybindings & Commands

### Keybindings
Configure under **Options > Controls > Key Binds > LovelyEasyPlace**:

| Action | Default Key | Description |
| :--- | :---: | :--- |
| **Open Configuration** | `O` | Opens the LovelyEasyPlace settings menu |
| **Toggle Mod State** | *Unbound* | Instantly enables or disables the mod |
| **Hold to Activate** | *Unbound* | Holds activation state while key is pressed |

### Client Commands
Available in both singleplayer and multiplayer console chat:

| Command | Action |
| :--- | :--- |
| `/lep` | Displays command help and active configuration state |
| `/lep toggle` | Toggles LovelyEasyPlace on or off |
| `/lep config` | Opens the in-game configuration GUI |
| `/lep reset` | Restores all configuration settings to factory defaults |

---

## ⚙️ Configuration Options

Settings are stored in `.minecraft/config/lovelyeasyplace.properties`.

### Primary Settings
- `enabled` (`true/false`): Master toggle for all placement helpers.
- `allowMultiplayer` (`true/false`): Enables placement features on multiplayer servers.
- `autoRotate` (`true/false`): Automatically aligns block rotation with Litematica schematics.
- `reversePlacement` (`true/false`): Places directional blocks facing away from the player when no schematic is active.
- `autoNoteBlockPitch` (`true/false`): Enables tick-queued Note Block auto-tuning.
- `matchRedstoneStates` (`true/false`): Matches Repeater delays and Comparator modes.
- `showHudIndicator` (`true/false`): Toggles the on-screen status text.

### Advanced Safety Properties
- `disabledServers`: Comma-separated list of server IP addresses where the mod must remain disabled.
- `minPlacementIntervalMs`: Minimum delay (in milliseconds, `0-1000`) enforced between automatic sneak interactions.
- `debugLogging`: Enables verbose debug logs in client output.

---

## 🛠️ Building from Source

Requires **JDK 21** installed.

```bash
# Clone repository
git clone https://github.com/lovelymod/LovelyEasyPlace.git
cd LovelyEasyPlace

# Build the mod JAR using Gradle
./gradlew build
```

The compiled mod JAR will be output to `build/libs/lovelyeasyplace-1.0.0.jar`.

---

## 📄 License

LovelyEasyPlace is distributed under the open-source **[MIT License](LICENSE)**.
