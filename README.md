# LovelyEasyPlace

**LovelyEasyPlace** is a client-side Fabric mod for Minecraft 1.21.11 that enhances building workflow, schematic placement, and redstone construction with automatic fake-sneaking, Litematica integration, reverse block placement, auto-restocking, and Carpet Accurate Placement protocol support.

---

## 🌟 Feature Overview

### 1. EasyPlace / Container Fake-Sneak
- Automatically fake-sneaks when placing blocks against interactive container and workstation blocks, preventing unintentional GUI popups without requiring you to hold Shift manually.
- **Supported Blocks**: Chests, Trapped Chests, Hoppers, Furnaces, Smokers, Blast Furnaces, Dispensers, Droppers, Barrels, Shulker Boxes, Crafters, Anvils, Crafting Tables, Ender Chests, Looms, Cartography Tables, Grindstones, Stonecutters, Smithing Tables, Brewing Stands, Beacons, Enchanting Tables, Lecterns, Chiseled Bookshelves, Jukeboxes, Note Blocks, Doors, Trapdoors, Fence Gates, Repeaters, Comparators, Levers, Buttons, and auto-detected custom GUI blocks.

### 2. Reverse Block Placement
- Places directional blocks (Pistons, Observers, Hoppers, Stairs, Logs, etc.) facing in the **opposite** orientation relative to the player.
- Configurable via keybinding, `/lep reverse` command, or directly inside the Config GUI panel.

### 3. Litematica Integration
- **Precise Auto Select Item**: Detects replaceable schematic ghost targets and automatically selects the required item from hotbar or main inventory. Gated specifically to valid schematic ghost targets.
- **Auto Rotate**: Copies block-state properties directly from the active schematic (facing, horizontal facing, hopper facing, axis, half, shape, hinge, orientation, face, attachment, rotation).
- **Adjacent / Edge Placement**: Places a nearby ghost block by clicking against an adjacent solid block.

### 4. Inventory Auto Restock
- Automatically refills your hand from hotbar or main inventory (slots 9–35) when a held item stack runs out after placement.

### 5. Redstone Component Synchronization
- **Match Repeater Delay**: Synchronizes repeater tick delay with the schematic target on the next tick.
- **Match Comparator Mode**: Synchronizes comparator mode (Compare / Subtract) with the schematic state.
- **Match Observer & Redstone States**: Preserves exact orientation for observers and redstone components according to user-configurable toggles.

### 6. Carpet Accurate Placement Protocol
- Encodes block facing into interaction hit vectors compatible with Carpet Mod's server-side accurate placement protocol, ensuring accurate orientation on multiplayer servers.

### 7. Config Panel & In-Game Controls
- In-game Config GUI powered by **Cloth Config** (Press **`O`** or access from the Pause Menu).
- Full customization for all settings without needing commands.

---

## ⌨️ Controls & Keybindings

| Action | Default Key |
| --- | --- |
| Open Config Panel | `O` |
| Toggle LovelyEasyPlace | Unbound |
| Hold-to-Activate Mode | Unbound |
| Toggle Reverse Placement | Unbound |

---

## ⚙️ Configuration Panel (`O` Key)

Access the configuration menu in-game by pressing **`O`** or via the **Pause Menu** button. Settings are categorized into:

1. **General Settings**:
   - Enable LovelyEasyPlace (Master Toggle)
   - Hold-to-Activate Mode
   - Show HUD Indicator
   - Debug Logging
2. **Schematic Placement**:
   - Reverse Placement
   - Litematica: Auto Select Item
   - Litematica: Auto Rotate
   - Litematica: Adjacent Placement
   - Auto Restock from Inventory
   - Match Repeater Delay
   - Match Comparator Mode
   - Match Observer Facing
   - Match Redstone Block States
   - Carpet Accurate Placement Protocol
3. **Supported Blocks**:
   - Per-block interaction bypass toggles for all container and interactive block types.

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
