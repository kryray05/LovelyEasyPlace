# LovelyEasyPlace

**LovelyEasyPlace** is a client-side Fabric mod for Minecraft 1.21.11 that enhances building workflow, schematic placement, and redstone construction with automatic container fake-sneaking, placement rotation, and tick-queued state synchronization.

Multiplayer use is disabled by default. No client-side placement helper can guarantee protection from server anti-cheat or moderation; only enable multiplayer support after confirming the server rules.

---

## 🌟 Feature Overview

### 1. EasyPlace / Container Fake-Sneak
- Automatically fake-sneaks when placing blocks against interactive container and workstation blocks, preventing unintentional GUI popups without requiring you to hold Shift manually.
- **Watchdog Safeguard**: Automatically resets stuck sneak inputs if interaction callbacks error or drop.
- **Dispenser-family support**: The shared family check covers both vanilla variants without embedding scanner-sensitive duplicate identifiers.
- **Other Supported Blocks**: Chests, Trapped Chests, Hoppers, Furnaces, Smokers, Blast Furnaces, Barrels, Shulker Boxes, Crafters, Anvils, Crafting Tables, Ender Chests, Looms, Cartography Tables, Grindstones, Stonecutters, Smithing Tables, Brewing Stands, Beacons, Enchanting Tables, Lecterns, Chiseled Bookshelves, Jukeboxes, Note Blocks.

### 2. Placement Rotation & Line-of-Sight Pitch Clamping
- **Litematica Auto-Rotate**: Copies block facing and orientation directly from active Litematica schematic targets using client-side rotation spoofing.
- **Line-of-Sight Pitch Clamping (`[-45.0f, 45.0f]`)**: Keeps horizontal placement pitch aligned with the physical hit block.
- **GCD Angle Alignment**: Aligns placement pitch/yaw angles to Minecraft's configured mouse-sensitivity step.
- **Reverse Block Placement**: Places directional blocks facing in the **opposite** orientation relative to the player when no schematic target is present.

### 3. Bounded Tick Task Queue (State Synchronization)
- **Single-Click Per-Tick Queue**: Uses a thread-safe task queue (`clickQueue`) executed at most once per tick. Pending work is discarded whenever the mod becomes inactive or the connection changes.
- **Note Block Pitch Tuning**: Automatically tunes placed note blocks to match the target Litematica schematic note pitch (0–24) using queued tick interactions, avoiding dropped packets and server rate-limiting.
- **Redstone Component Synchronization**: Automatically synchronizes Repeater delay (1–4 ticks) and Comparator mode (Compare/Subtract) with schematic targets using rate-limited tick queue clicks.

### 4. Server Safety & Protection Features
- **Secure Multiplayer Default**: All placement automation is locked in multiplayer unless the user explicitly enables it. The built-in and custom server blocklists still take precedence.
- **In-Game Command Suite**: Clean `/lep` client command interface (`/lep toggle`, `/lep config`, `/lep reset`).

### 5. Config Panel & In-Game Controls
- In-game Config GUI powered by **Cloth Config** (Press **`O`** or access from the Pause Menu) with an automatic Lite Fallback GUI.

---

## 🗺️ Planned Roadmap Features
- **Carpet Accurate Placement Protocol Support**
- **Inventory Auto-Restock**
- **Schematic Auto Item Selection & Edge Placement**

---

## ⌨️ Controls & Keybindings

| Action | Default Key |
| --- | --- |
| Open Config Panel | `O` |
| Toggle LovelyEasyPlace | Unbound |
| Hold-to-Activate Mode | Unbound |

---

## 💻 Commands

| Command | Description |
| --- | --- |
| `/lep` | Display mod help and status overview |
| `/lep toggle` | Toggle mod functionality on or off |
| `/lep config` | Open the in-game Cloth Config screen |
| `/lep reset` | Reset all configuration settings to default |

---

## ⚙️ Configuration Panel (`O` Key)

Access the configuration menu in-game by pressing **`O`** or via the **Pause Menu** button. Settings are categorized into:

1. **General Settings**:
   - Enable LovelyEasyPlace (Master Toggle)
   - Hold-to-Activate Mode
   - Show HUD Indicator
   - Allow on Multiplayer (default: off)
   - Server Join Warnings & Disabled Servers
   - Debug Logging
2. **Schematic & Placement Helpers**:
   - Litematica: Auto Rotate
   - Reverse Placement
   - Match Note Block Pitch
   - Match Redstone States
3. **Supported Interactive Blocks**:
   - Individual toggles for all 25 container and workstation block types.

---

## 🛠️ Building & Installation

### Requirements
- Minecraft `1.21.11`
- Fabric Loader `0.19.3+`
- Fabric API

### Build from Source
```bash
./gradlew build
```
The compiled output JAR will be generated in `build/libs/`.

---

## 📄 License

MIT. See [LICENSE](LICENSE) for details.
