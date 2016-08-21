package com.example.florian.bluetoothvolumeadapter.Database;

/**
 * Created by Florian on 11/08/2016.
 */
public class DeviceOptions {
    String adress;
    String name;
    int activated;
    int rememberLastVolume;
    int volume;

    public DeviceOptions(String a, String n) {
        adress = a;
        name = n;
    }

    public void setActivated(int a) {
        activated = a;
    }
    public void setRememberLastVolume(int r) {
        rememberLastVolume = r;
    }
    public void setVolume(int v) {
        volume = v;
    }

    public String getAdress() {
        return adress;
    }
    public String getName() {
        return name;
    }
    public int getActivated() {
        return activated;
    }
    public int getRememberLastVolume() {
        return rememberLastVolume;
    }
    public int getVolume() {
        return volume;
    }
}
