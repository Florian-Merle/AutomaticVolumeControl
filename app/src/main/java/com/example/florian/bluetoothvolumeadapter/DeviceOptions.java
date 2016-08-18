package com.example.florian.bluetoothvolumeadapter;

/**
 * Created by Florian on 11/08/2016.
 */
public class DeviceOptions {
    String adresse;
    String name;
    int activated;
    int rememberLastVolume;
    int volume;

    public DeviceOptions(String a, String n) {
        adresse = a;
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

    public String getAdresse() {
        return adresse;
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
