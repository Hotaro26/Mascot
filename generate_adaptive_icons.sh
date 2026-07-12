#!/bin/bash
mkdir -p app/src/main/res/mipmap-anydpi-v26
for i in 1 2 4 5; do
  # Generate foreground PNG from SVG. Slightly smaller this time (480x480 on 512x512 canvas)
  rsvg-convert -w 480 -h 480 "mascot icons/mascot_icon${i}.svg" | convert - -gravity center -extent 512x512 "app/src/main/res/mipmap-xxxhdpi/mascot_icon${i}_fg.png"
  
  # Create adaptive icon XML with a new name so it doesn't conflict with AppIconsScreen compose images
  cat << XML > "app/src/main/res/mipmap-anydpi-v26/ic_launcher_mascot${i}.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black"/>
    <foreground android:drawable="@mipmap/mascot_icon${i}_fg"/>
</adaptive-icon>
XML
done

rsvg-convert -w 480 -h 480 "mascot icons/mascot_iccon3.svg" | convert - -gravity center -extent 512x512 "app/src/main/res/mipmap-xxxhdpi/mascot_icon3_fg.png"
cat << XML > "app/src/main/res/mipmap-anydpi-v26/ic_launcher_mascot3.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black"/>
    <foreground android:drawable="@mipmap/mascot_icon3_fg"/>
</adaptive-icon>
XML
