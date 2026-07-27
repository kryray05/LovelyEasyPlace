#!/bin/bash

# LovelyEasyPlace Build Script
# Builds the Fabric mod for Minecraft 1.21.11.

set -euo pipefail

echo "Building LovelyEasyPlace..."
echo "==========================="

if [ -x "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
elif command -v gradle >/dev/null 2>&1; then
    GRADLE_CMD="gradle"
else
    echo "Gradle was not found and this project has no Gradle wrapper."
    echo "Install Gradle 9.5.0+, or generate a wrapper with: gradle wrapper --gradle-version 9.5.0"
    exit 1
fi

echo
echo "Running Gradle build..."
"${GRADLE_CMD}" build

echo
echo "Build complete."
BUILT_JAR="build/libs/lovelyeasyplace-1.0.0.jar"
echo "JAR: $BUILT_JAR"

# Automated Lunar Client installation
LUNAR_PROFILES_DIR="/home/raymond/snap/lunar-client/common/.lunarclient/profiles/lunar"
if [ -d "$LUNAR_PROFILES_DIR" ]; then
    echo
    echo "Detected Lunar Client profile directory. Installing mod..."
    if [ -f "$BUILT_JAR" ]; then
        # Find all fabric-* mods directories under lunar profile
        found_any=false
        for dir in $(find "$LUNAR_PROFILES_DIR" -type d -name "fabric-*" 2>/dev/null); do
            echo "Installing to $dir..."
            # Clean up old jars to prevent duplication/conflict issues
            rm -f "$dir"/lovelyeasyplace-*.jar
            cp "$BUILT_JAR" "$dir/"
            echo "Successfully copied to $dir"
            found_any=true
        done
        if [ "$found_any" = false ]; then
            echo "Warning: No fabric-* folders found in Lunar profiles."
        fi
    else
        echo "Error: Built JAR not found at $BUILT_JAR"
        exit 1
    fi
else
    echo "Lunar Client profiles directory not found at $LUNAR_PROFILES_DIR"
fi
