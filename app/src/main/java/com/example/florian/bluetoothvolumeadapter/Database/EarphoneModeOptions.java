package com.example.florian.bluetoothvolumeadapter.Database;

/**
 * Created by Florian on 24/08/2016.
 */
public class EarphoneModeOptions {
    String name;
    int volume;

    public EarphoneModeOptions(String name, int volume) {
        this.name = name;
        this.volume = volume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
