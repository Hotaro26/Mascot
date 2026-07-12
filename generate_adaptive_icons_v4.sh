#!/bin/bash
for i in 1 2 4 5; do
  cat << XML > "app/src/main/res/mipmap-anydpi-v26/ic_launcher_mascot${i}.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/mascot${i}_bg"/>
    <foreground android:drawable="@mipmap/mascot_icon${i}_fg"/>
</adaptive-icon>
XML
done

cat << XML > "app/src/main/res/mipmap-anydpi-v26/ic_launcher_mascot3.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/mascot3_bg"/>
    <foreground android:drawable="@mipmap/mascot_icon3_fg"/>
</adaptive-icon>
XML

echo "Updated XML files"
./gradlew installDebug
