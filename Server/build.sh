#!/bin/sh
chmod +x gradlew
mkdir -p output
cp ./startup.sh output 2>/dev/null
chmod +x output/startup.sh
./gradlew clean build #--stacktrace --refresh-dependencies
cp ./build/libs/*.jar output
cp -r ./conf output
if [ -d "./data" ]; then
  cp -r ./data output
fi
