package com.example.florian.bluetoothvolumeadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Florian on 24/08/2016.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    private final Fragment mBluetoothDevicesListFragment;
    private final Fragment mEarphoneProfilesListFragment;

    public PageAdapter(FragmentManager fm) {
        super(fm);
        mBluetoothDevicesListFragment = new bluetoothDevicesListFragment();
        mEarphoneProfilesListFragment = new bluetoothDevicesListFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position) {
            case 0:
                f = mBluetoothDevicesListFragment;
                break;
            case 1:
                f = mEarphoneProfilesListFragment;
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public Fragment getBluetoothDevicesListFragment() {
        return mBluetoothDevicesListFragment;
    }
    public Fragment getEarphoneProfilesListFragment() {
        return mEarphoneProfilesListFragment;
    }
}
