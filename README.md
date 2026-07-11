# LovelyEasyPlace

LovelyEasyPlace is a client-side Fabric quality-of-life mod for Minecraft 1.21.11. It helps you place blocks against chests, hoppers, barrels, furnaces, and similar interactive blocks without holding sneak.

## Features

- Place a second chest for a double chest without holding Shift
- Place blocks against hoppers, barrels, furnaces, dispensers, droppers, and trapped chests without opening their GUI
- Only activates when you are holding a block item, so empty-hand and tool interactions still open containers normally
- Per-block toggles through Mod Menu and Cloth Config
- Optional keybind to enable or disable the mod in-game
- HUD indicator for the current active, inactive, or server-blocked state
- Optional server blacklist to auto-disable the mod on selected multiplayer servers
- One-time multiplayer join warning and `/lep config` client command
- Optional hold-to-activate mode and debug logging

## Supported Blocks

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

## How It Works

When you right-click a supported block while holding a block item, LovelyEasyPlace briefly sends the normal vanilla player-input packet with sneak enabled, lets the placement interaction happen, then restores your previous input state if you were not already sneaking.

This is similar in concept to Tweakeroo's Fake Sneak and Omni-Hopper's placement helper idea, but this mod is focused only on convenient block placement. It does not hide itself, bypass anti-cheat, or make the server accept anything vanilla sneaking would not normally allow.

LovelyEasyPlace does not add randomized packet timing or anti-cheat evasion behavior. For multiplayer servers, use the server blacklist, the join warning, and hold-to-activate mode, and only use the mod where client-side QoL helpers are allowed.

## Installation

1. Install Fabric Loader 0.19.3 or newer for Minecraft 1.21.11.
2. Install Fabric API for Minecraft 1.21.11.
3. Put `lovelyeasyplace-1.0.0.jar` in your `mods` folder.

Cloth Config is bundled in the mod jar. Mod Menu is optional, but recommended for the config screen.

## Configuration

With Mod Menu installed, open LovelyEasyPlace from the mod list and click Configure.

You can also edit `config/lovelyeasyplace.properties`:

```properties
enabled=true
placeOnChests=true
placeOnHoppers=true
placeOnFurnaces=true
placeOnDispensers=true
placeOnDroppers=true
placeOnTrappedChests=true
placeOnBarrels=true
placeOnShulkerBoxes=true
placeOnSmokers=true
placeOnBlastFurnaces=true
showHudIndicator=true
warnOnServerJoin=true
holdMode=false
debugLogging=false
minPlacementIntervalMs=0
disabledServers=
warnedServers=
```

The toggle and hold keys are unbound by default. Set them in Controls -> Key Binds -> LovelyEasyPlace.

Client commands:

```text
/lep config
/lep toggle
/lep reset
```

## Building

```bash
./gradlew build
```

If the Gradle wrapper is not present, install Gradle and run:

```bash
gradle build
```

The output jar is written to `build/libs/lovelyeasyplace-1.0.0.jar`.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3 or newer
- Fabric API 0.141.4+1.21.11 or newer for 1.21.11
- Java 21 or newer

## License

MIT License. See `LICENSE` for details.
