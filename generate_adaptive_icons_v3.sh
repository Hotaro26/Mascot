#!/bin/bash
mkdir -p app/src/main/res/mipmap-anydpi-v26

# Map of background colors
declare -A colors
colors[1]="#F5F7F5"
colors[2]="#FEFDFD"
colors[3]="#FEFDFD"
colors[4]="#FEFDFD"
colors[5]="#111724"

for i in 1 2 4 5; do
  # Generate smaller foreground PNG (360x360) so nothing gets cropped
  rsvg-convert -w 360 -h 360 "mascot icons/mascot_icon${i}.svg" | convert - -gravity center -background transparent -extent 512x512 "app/src/main/res/mipmap-xxxhdpi/mascot_icon${i}_fg.png"
  
  bg_color=${colors[$i]}
  
  cat << XML > "app/src/main/res/mipmap-anydpi-v26/ic_launcher_mascot${i}.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="$bg_color"/>
    <foreground android:drawable="@mipmap/mascot_icon${i}_fg"/>
</adaptive-icon>
XML
done

rsvg-convert -w 360 -h 360 "mascot icons/mascot_iccon3.svg" | convert - -gravity center -background transparent -extent 512x512 "app/src/main/res/mipmap-xxxhdpi/mascot_icon3_fg.png"
bg_color=${colors[3]}
cat << XML > "app/src/main/res/mipmap-anydpi-v26/ic_launcher_mascot3.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="$bg_color"/>
    <foreground android:drawable="@mipmap/mascot_icon3_fg"/>
</adaptive-icon>
XML

echo "Perfect adaptive icons created successfully"
./gradlew installDebug
