package com.example.florian.bluetoothvolumeadapter;

import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.florian.bluetoothvolumeadapter.Database.DeviceDAO;
import com.example.florian.bluetoothvolumeadapter.Database.DeviceOptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Florian on 20/08/2016.
 */
public class BluetoothWatchService extends Service {
    public static final String STORAGE_FILE = "BVA_volume_file";

    private DeviceDAO mDeviceDAO;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(mReceiver);
    }

    //bluetooth broadcast receiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public boolean mConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                if(state == BluetoothA2dp.STATE_PLAYING) {
                    if (mConnected == false) {
                        deviceConnected(d);
                        mConnected = true;
                    }
                }
            }
            else if(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                if(state == BluetoothA2dp.STATE_DISCONNECTED) {
                    mConnected = false;
                    deviceDisconnected(d);
                }
            }
        }
    };

    private void deviceConnected(BluetoothDevice bluetoothDevice) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showToasts = prefs.getBoolean("show_toasts", false);
        if (showToasts) { Toast.makeText(this, getResources().getString(R.string.connected_to) + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show(); }

        mDeviceDAO = new DeviceDAO(this);
        mDeviceDAO.open();

        DeviceOptions device = mDeviceDAO.select(bluetoothDevice.getAddress());
        if (device == null) { return; }

        if(device.getActivated() == 0) {
            return;
        }

        int v = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        saveLastVolume(v);

        int volume = device.getVolume();

        int flag = 0;
        if(prefs.getBoolean("show_am_ui", false)) {
            flag = AudioManager.FLAG_SHOW_UI;
        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume,
                flag);
    }

    private void deviceDisconnected(BluetoothDevice bluetoothDevice) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showToasts = prefs.getBoolean("show_toasts", false);
        if (showToasts) { Toast.makeText(this,getResources().getString(R.string.disconnected_from), Toast.LENGTH_SHORT).show(); }

        mDeviceDAO = new DeviceDAO(this);
        mDeviceDAO.open();

        DeviceOptions device = mDeviceDAO.select(bluetoothDevice.getAddress());
        if (device == null) { return; }

        if(device.getActivated() == 0) {
            return;
        }

        //save volume
        if(device.getRememberLastVolume() ==  1) {
            int v = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            device.setVolume(v);
            mDeviceDAO.update(device);
        }

        //change volume back
        int volume = getLastVolume();

        int flag = 0;
        if(prefs.getBoolean("show_am_ui", false)) {
            flag = AudioManager.FLAG_SHOW_UI;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume,
                flag);
    }

    private void saveDeviceVolume(BluetoothDevice bluetoothDevice) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mDeviceDAO = new DeviceDAO(this);
        mDeviceDAO.open();

        DeviceOptions device = mDeviceDAO.select(bluetoothDevice.getAddress());
        if (device == null) { return; }

        if(device.getActivated() == 0) {
            return;
        }


    }

    private void saveLastVolume(int v) {
        try {
            FileOutputStream fos = openFileOutput(STORAGE_FILE, Context.MODE_PRIVATE);
            fos.write((v+"").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getLastVolume() {
        try {
            FileInputStream fis = openFileInput(STORAGE_FILE);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = fis.read()) != -1) {
                sb.append((char) ch);
            }

            fis.close();
            return Integer.parseInt(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
