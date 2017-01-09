#!/bin/sh

echo "Generating png images from svg."
inkscape -z -e trails48.png -w 48 -h 48 trails.svg > /dev/null
inkscape -z -e trails72.png -w 72 -h 72 trails.svg > /dev/null
inkscape -z -e trails96.png -w 96 -h 96 trails.svg > /dev/null
inkscape -z -e trails144.png -w 144 -h 144 trails.svg > /dev/null
inkscape -z -e trails192.png -w 192 -h 192 trails.svg > /dev/null

echo "Moving files into correct location."
mv trails48.png ../Trails/app/src/main/res/mipmap-mdpi/ic_launcher.png
mv trails72.png ../Trails/app/src/main/res/mipmap-hdpi/ic_launcher.png
mv trails96.png ../Trails/app/src/main/res/mipmap-xhdpi/ic_launcher.png
mv trails144.png ../Trails/app/src/main/res/mipmap-xxhdpi/ic_launcher.png
mv trails192.png ../Trails/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png

echo "Done."

