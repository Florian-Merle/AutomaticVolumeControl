package com.example.florian.bluetoothvolumeadapter;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.florian.bluetoothvolumeadapter.Database.DeviceDAO;
import com.example.florian.bluetoothvolumeadapter.Database.DeviceOptions;

/**
 * Created by Florian on 20/08/2016.
 */
public class BluetoothWatchService extends Service {
    private DeviceDAO mDeviceDAO;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, R.string.service_started, Toast.LENGTH_SHORT).show();

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
        Toast.makeText(this, getResources().getString(R.string.connected_to) + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();

        mDeviceDAO = new DeviceDAO(this);
        mDeviceDAO.open();

        DeviceOptions device = mDeviceDAO.select(bluetoothDevice.getAddress());
        if (device == null) { return; }

        if(device.getActivated() == 0) {
            return;
        }

        int volume = (device.getVolume() * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 100;

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume,
                AudioManager.FLAG_SHOW_UI);
    }

    private void deviceDisconnected(BluetoothDevice bluetoothDevice) {
        Toast.makeText(this,getResources().getString(R.string.disconnected_from) + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();

    }
}
