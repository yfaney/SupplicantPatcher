stty -echo
mount -o rw, remount /system
ifconfig wlan0 down
cp $1/hostapd /system/bin
chmod 755 hostapd
chown root:shell hostapd
cp $1/wpa_supplicant /system/bin
chmod 755 wpa_supplicant
chown root:shell wpa_supplicant
mount -o ro, remount /system
stty echo