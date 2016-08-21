package com.example.florian.bluetoothvolumeadapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.florian.bluetoothvolumeadapter.Database.DeviceDAO;
import com.example.florian.bluetoothvolumeadapter.Database.DeviceOptions;

/**
 * Created by Florian on 11/08/2016.
 */
public class EditDeviceBehaviorActivity extends AppCompatActivity {
    private TextView mDeviceNameView;
    private SeekBar mVolumeSeekbar;
    private Switch mActivateDeviceSwitch;
    private Switch mRememberVolumeSwitch;

    private DeviceDAO mDeviceDAO;
    private DeviceOptions mDevice;

    private int mDefaultVolumeValue = 0;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device_behavior);

        Intent intent = getIntent();
        String mName = intent.getStringExtra(MainActivity.EXTRA_DEVICE_NAME);
        String mAddress = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        mDeviceDAO = new DeviceDAO(this);
        mDeviceDAO.open();

        mDevice = mDeviceDAO.select(mAddress);
        if(mDevice == null) { // create device in database
            mDevice = new DeviceOptions(mAddress, mName);
            mDevice.setVolume(mDefaultVolumeValue);
            mDeviceDAO.insert(mDevice);
        }

        // INITIALIZE STUFF
        /* tool bar */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(R.string.edb_title);
        getSupportActionBar().setSubtitle(mDevice.getName());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* other stuff */
        mVolumeSeekbar = (SeekBar) findViewById(R.id.volumeSeekBar);
        mVolumeSeekbar.setProgress(mDevice.getVolume());

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeekbar.setMax(max);
        mVolumeSeekbar.invalidate();

        mActivateDeviceSwitch = (Switch) findViewById(R.id.activateDevice);
        mActivateDeviceSwitch.setChecked((mDevice.getActivated() != 0));
        mActivateDeviceSwitch.invalidate();

        mRememberVolumeSwitch = (Switch) findViewById(R.id.rememberVolume);
        mRememberVolumeSwitch.setChecked((mDevice.getRememberLastVolume() != 0));
        mRememberVolumeSwitch.invalidate();

        if(mDevice.getActivated() == 0) {
            mRememberVolumeSwitch.setEnabled(false);
            mVolumeSeekbar.setEnabled(false);
        }

        //SEEKBAR CHANGED
        mVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDevice.setVolume(i);
                updateDevice();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //SWITCHES CHANGED
        mActivateDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDevice.setActivated((b) ? 1 : 0);
                updateDevice();

                if(!b) {
                    mRememberVolumeSwitch.setEnabled(false);
                    mVolumeSeekbar.setEnabled(false);
                }
                else {
                    mRememberVolumeSwitch.setEnabled(true);
                    mVolumeSeekbar.setEnabled(true);
                }
            }
        });
        mRememberVolumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDevice.setRememberLastVolume((b) ? 1 : 0);
                updateDevice();
            }
        });
    }

    private void updateDevice() {
        mDeviceDAO.update(mDevice);
    }
}
