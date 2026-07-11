#!/bin/bash
# Entrypoint script for LovelyEasyPlace.

cat << 'EOF'
LovelyEasyPlace v1.0.0
======================

LovelyEasyPlace is a Fabric client mod for Minecraft 1.21.11 that helps
you place blocks against chests, hoppers, barrels, furnaces, and similar
interactive blocks without holding Shift.

Usage
-----
- Hold any block item.
- Right-click a configured interactive block.
- The mod briefly performs a normal vanilla sneak interaction so placement
  happens instead of opening the block GUI.

Supported blocks
----------------
- Chests
- Trapped chests
- Hoppers
- Furnaces
- Dispensers
- Droppers
- Barrels

Build
-----
Run:

  ./build.sh

Output:

  build/libs/lovelyeasyplace-1.0.0.jar

Configuration
-------------
- Mod Menu config screen, if Mod Menu is installed
- config/lovelyeasyplace.properties
- Controls -> Key Binds -> LovelyEasyPlace for the optional toggle key

This is a quality-of-life placement helper. It does not bypass anti-cheat
or hide its behavior.
EOF
