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
echo "JAR: build/libs/lovelyeasyplace-1.0.0.jar"
