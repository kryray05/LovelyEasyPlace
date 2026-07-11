# LovelyEasyPlace

LovelyEasyPlace is a client-side Fabric mod for Minecraft 1.21.11 that makes it easier to place blocks against containers and other interactive storage blocks. When you place a block against a supported target, the mod temporarily uses the normal vanilla sneak input so the target's interface does not open.

GitHub: [kryray05/LovelyEasyPlace](https://github.com/kryray05/LovelyEasyPlace)

## Features

- Automatically fake-sneak while placing blocks against supported containers
- Create double chests without manually holding the sneak key
- Activate only when the held item is a block, leaving empty-hand and tool interactions unchanged
- Support chests, trapped chests, hoppers, furnaces, smokers, blast furnaces, dispensers, droppers, barrels, and shulker boxes
- Enable or disable every supported block type independently
- Search the supported-block list from the Cloth Config screen
- Open **Cấu hình LovelyEasyPlace** from Mod Menu, the pause menu, or the `O` key
- Toggle the entire mod with an optional keybind or `/lep toggle`
- Use an optional hold-to-activate mode instead of the persistent toggle
- Show an optional HUD indicator for enabled, disabled, and server-blocked states
- Automatically disable the mod on configured multiplayer servers
- Show a one-time warning when joining a multiplayer server
- Configure an optional minimum interval between assisted placements
- Enable debug logging for troubleshooting
- Reset every setting with `/lep reset`
- Save settings in `config/lovelyeasyplace.properties`
- Run entirely on the client; servers do not need to install the mod

## Supported Blocks

LovelyEasyPlace is intentionally limited to these blocks:

- Chests
- Trapped chests
- Hoppers
- Furnaces
- Smokers
- Blast furnaces
- Dispensers
- Droppers
- Barrels
- Shulker boxes

It does not automatically activate on every interactive block.

## Using the Block Selector

Open **Cấu hình LovelyEasyPlace** using any of these methods:

- Press `O` in game (default configuration key)
- Click **LEP Config** in the pause menu
- Open LovelyEasyPlace in Mod Menu and click **Configure**

The menu contains only the ten supported block types. Switch a block type on or off, then click **Save & Quit** to apply and save the selection.

## How It Works

When you right-click a supported block while holding a block item, LovelyEasyPlace briefly sends the standard player input with sneaking enabled. It performs the placement interaction and then restores your previous input state when you were not already sneaking.

Normal empty-hand and tool interactions are unaffected. The mod does not bypass server validation or make the server accept an action that normal vanilla sneaking would reject.

Always check a multiplayer server's rules before using client-side quality-of-life mods.

## Installation

1. Install Minecraft 1.21.11 with Fabric Loader 0.19.3 or newer.
2. Install Fabric API for Minecraft 1.21.11.
3. Copy `lovelyeasyplace-1.0.0.jar` into the Minecraft `mods` directory.
4. Optionally install Mod Menu for access through the installed-mod list.

Cloth Config is bundled inside the LovelyEasyPlace jar.

## Keybinds and Commands

Keybinds are available under **Options → Controls → Key Binds → LovelyEasyPlace**:

| Action | Default |
| --- | --- |
| Open LovelyEasyPlace Config | `O` |
| Toggle LovelyEasyPlace | Unbound |
| Hold LovelyEasyPlace | Unbound |

Client commands:

```text
/lep toggle
/lep reset
```

## Configuration File

Settings are stored in `config/lovelyeasyplace.properties`. The block selector manages the `placeOn...` values. Advanced settings can still be edited directly in this file.

Multiplayer join warnings are always enabled. LovelyEasyPlace is also permanently disabled on Hypixel, MCC Island, CubeCraft, Wynncraft, and ManaCube; these built-in safety entries cannot be removed through the configuration file. `disabledServers` can still be used to add more servers.

```properties
enabled=true
placeOnChests=true
placeOnTrappedChests=true
placeOnHoppers=true
placeOnFurnaces=true
placeOnSmokers=true
placeOnBlastFurnaces=true
placeOnDispensers=true
placeOnDroppers=true
placeOnBarrels=true
placeOnShulkerBoxes=true
showHudIndicator=true
warnOnServerJoin=true
holdMode=false
debugLogging=false
minPlacementIntervalMs=0
disabledServers=
warnedServers=
```

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3 or newer
- Fabric API 0.141.4+1.21.11 or compatible
- Java 21 or newer

## Building

Build with the Gradle wrapper when available:

```bash
./gradlew build
```

Otherwise, install Gradle 9.5.0 or newer and run:

```bash
gradle build
```

The generated mod jar is written to `build/libs/lovelyeasyplace-1.0.0.jar`.

## Releasing

The GitHub Actions workflow builds every push and pull request. Push a version tag to build the project and automatically publish the compiled mod JAR in a GitHub Release:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The release title and generated release notes use the pushed tag. Regular branch builds are available as workflow artifacts but do not create releases.

## License

LovelyEasyPlace is maintained by [kryray05](https://github.com/kryray05) and is available under the MIT License. See [LICENSE](LICENSE) for details.
