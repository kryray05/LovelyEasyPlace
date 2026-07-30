# LovelyEasyPlace Build Instructions

LovelyEasyPlace targets Minecraft 1.21.11 on Fabric.

## Prerequisites

- Java 21 or newer
- Gradle 9.5.0 or newer, or a generated Gradle wrapper

## Build

```bash
./build.sh
```

Or run Gradle directly:

```bash
gradle build
```

The mod jar is written to:

```text
build/libs/lovelyeasyplace-1.0.0.jar
```

## Development

Import the project as a Gradle project in IntelliJ IDEA, then run:

```bash
gradle genSources
gradle runClient
```

## Project Layout

```text
src/main/java/com/lovelyeasyplace/
  LovelyEasyPlaceMod.java
  config/LovelyEasyPlaceConfig.java
  integration/ModMenuIntegration.java
  mixin/ClientPlayerEntityMixin.java
  mixin/ClientPlayerInteractionManagerMixin.java

src/main/resources/
  fabric.mod.json
  lovelyeasyplace.mixins.json
  assets/lovelyeasyplace/lang/en_us.json
```

## Version Pins

- Minecraft: 1.21.11
- Yarn mappings: 1.21.11+build.6
- Fabric Loader: 0.19.3
- Fabric API: 0.141.4+1.21.11
- Cloth Config: 21.11.153
- Mod Menu compile-only API: 17.0.0
