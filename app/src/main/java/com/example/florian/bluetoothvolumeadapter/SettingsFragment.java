package com.example.florian.bluetoothvolumeadapter;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Florian on 21/08/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
