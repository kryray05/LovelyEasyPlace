# LovelyEasyPlace

**LovelyEasyPlace** is a client-side Fabric mod for Minecraft 1.21.11 that enhances building workflow, schematic placement, and redstone construction with automatic fake-sneaking, Litematica rotation spoofing, reverse block placement, and state matching.

---

## 🌟 Feature Overview

### 1. EasyPlace / Container Fake-Sneak
- Automatically fake-sneaks when placing blocks against interactive container and workstation blocks, preventing unintentional GUI popups without requiring you to hold Shift manually.
- **25 Supported Blocks**: Chests, Trapped Chests, Hoppers, Furnaces, Smokers, Blast Furnaces, Dispensers, Droppers, Barrels, Shulker Boxes, Crafters, Anvils, Crafting Tables, Ender Chests, Looms, Cartography Tables, Grindstones, Stonecutters, Smithing Tables, Brewing Stands, Beacons, Enchanting Tables, Lecterns, Chiseled Bookshelves, Jukeboxes, Note Blocks.

### 2. Litematica Auto-Rotate
- Copies block facing and orientation directly from active Litematica schematic targets using client rotation spoofing. Supports horizontal and vertical block placement including Observers, Pistons, Crafters, Dispensers, Droppers, Barrels, Repeaters, Comparators, Stairs, Doors, Anvils, and Beds.

### 3. Reverse Block Placement
- Places directional blocks facing in the **opposite** orientation relative to the player when no schematic target is present.

### 4. Redstone & Note Block State Synchronization
- **Match Redstone States**: Automatically synchronizes Repeater delay and Comparator mode with the schematic state upon placement.
- **Match Note Block Pitch**: Automatically tunes placed note blocks to match the target schematic note pitch, absorbing extra right-clicks to prevent detuning.

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

## ⚙️ Configuration Panel (`O` Key)

Access the configuration menu in-game by pressing **`O`** or via the **Pause Menu** button. Settings are categorized into:

1. **General Settings**:
   - Enable LovelyEasyPlace (Master Toggle)
   - Hold-to-Activate Mode
   - Show HUD Indicator
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
