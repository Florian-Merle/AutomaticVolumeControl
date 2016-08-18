package com.example.florian.bluetoothvolumeadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Florian on 11/08/2016.
 */
public class EditDeviceBehaviorActivity extends Activity {
    private TextView mDeviceNameView;
    private AudioManager mAudioManger;
    private DeviceDAO mDeviceDAO;
    private DeviceOptions mDevice;

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
            mDeviceDAO.insert(mDevice);
        }

        mDeviceNameView = (TextView) findViewById(R.id.deviceName);
        mDeviceNameView.setText(mDevice.getName());
        mDeviceNameView.invalidate();

        mAudioManger = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }
}
