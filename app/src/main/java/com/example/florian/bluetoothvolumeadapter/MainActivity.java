package com.example.florian.bluetoothvolumeadapter;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Created by Florian on 24/08/2016.
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    public static final String EXTRA_DEVICE_ADDRESS = "device.adress";
    public static final String EXTRA_DEVICE_NAME = "device.name";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private PageAdapter mAdapter;
    private Toolbar mToolbar;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText(getResources().getString(R.string.bt_devices));
        mTabLayout.getTabAt(1).setText(getResources().getString(R.string.earphone_modes));

        /* tool bar */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setIcon(R.drawable.ic_toolbar_icon);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) { //device doesn't support bluetooth
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) { //request bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Intent intent = getIntent();
        int tabToOpen = intent.getIntExtra("tab", -1);
        if (tabToOpen!=-1) {
            mTabLayout.getTabAt(1).select();
        }

        //START SERVICE IF NOT STARTED
        Intent serviceIntent = new Intent(this, BluetoothWatchService.class);
        this.startService(serviceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if(mBluetoothAdapter == null) { //device doesn't support bluetooth
                    return;
                }
                bluetoothDevicesListFragment f = (bluetoothDevicesListFragment) mAdapter.getBluetoothDevicesListFragment();
                f.searchPairedDevices();
                break;
        }
    }

    public void action_settings(View view) {
        Intent intent = new Intent(this , SettingsActivity.class);
        startActivity(intent);
    }

    public void action_new_earphone_mode(View view) {
        earphoneProfilesListFragment f = (earphoneProfilesListFragment) mAdapter.getEarphoneProfilesListFragment();
        f.action_new_earphone_mode(view);
    }
}
