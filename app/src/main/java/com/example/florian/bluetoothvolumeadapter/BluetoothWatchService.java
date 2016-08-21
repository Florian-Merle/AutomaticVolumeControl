package com.example.florian.bluetoothvolumeadapter;

import android.app.Service;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Florian on 20/08/2016.
 */
public class BluetoothWatchService extends Service {
    public static final String STORAGE_FILE = "volume_file";

    private DeviceDAO mDeviceDAO;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, R.string.service_started, Toast.LENGTH_SHORT).show();

        IntentFilter connectFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mReceiver, connectFilter);

        IntentFilter disconnectFilter  = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, disconnectFilter);

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
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                deviceConnected(d);
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                deviceDisconnected(d);
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
        if (showToasts) { Toast.makeText(this,getResources().getString(R.string.disconnected_from) + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show(); }

        mDeviceDAO = new DeviceDAO(this);
        mDeviceDAO.open();

        DeviceOptions device = mDeviceDAO.select(bluetoothDevice.getAddress());
        if (device == null) { return; }

        if(device.getActivated() == 0) {
            return;
        }

        if(device.getRememberLastVolume() ==  1) {
            int v = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            device.setVolume(v);
            mDeviceDAO.update(device);
        }

        int volume = getLastVolume();

        int flag = 0;
        if(prefs.getBoolean("show_am_ui", false)) {
            flag = AudioManager.FLAG_SHOW_UI;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume,
                flag);
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
