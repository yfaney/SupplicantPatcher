stty -echo
mount -o rw, remount /system
ifconfig wlan0 down
cp ./backup/hostapd /system/bin
chmod 755 hostapd
chown root:shell hostapd
cp ./backup/wpa_supplicant /system/bin
chmod 755 wpa_supplicant
chown root:shell wpa_supplicant
mount -o ro, remount /system
stty echo