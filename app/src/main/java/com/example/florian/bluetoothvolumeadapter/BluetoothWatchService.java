package com.example.florian.bluetoothvolumeadapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.florian.bluetoothvolumeadapter.Database.DeviceDAO;
import com.example.florian.bluetoothvolumeadapter.Database.DeviceOptions;
import com.example.florian.bluetoothvolumeadapter.Database.EarphoneModeDAO;
import com.example.florian.bluetoothvolumeadapter.Database.EarphoneModeOptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Florian on 20/08/2016.
 */
public class BluetoothWatchService extends Service {
    //TODO Save connected

    public static final String STORAGE_FILE = "BVA_volume_file";

    private DeviceDAO mDeviceDAO;
    private AudioManager mAudioManager;

    private EarphoneModeDAO mEarphoneModeDAO;
    private boolean mConnected = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
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
            else if(Intent.ACTION_HEADSET_PLUG.equals(action)) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Boolean v = sharedPreferences.getBoolean("earphones_modes_activated", false);
                if(v) {
                    int state = intent.getIntExtra("state", -1);
                    if(state >= 1) {
                        mEarphoneModeDAO = new EarphoneModeDAO(context);
                        mEarphoneModeDAO.open();

                        List<EarphoneModeOptions> result = mEarphoneModeDAO.selectAll();

                        if (result.size() != 0) {
                            //create notification
                            EarphoneModeChooserNotificationManager.createNotification(context);
                        }
                    }
                    else {
                        EarphoneModeChooserNotificationManager.deleteNotification(context);
                    }
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
        if (showToasts) { Toast.makeText(this,getResources().getString(R.string.disconnected_from) + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show(); }

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
