package com.example.florian.bluetoothvolumeadapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    public static final String EXTRA_DEVICE_ADDRESS = "device.adress";
    public static final String EXTRA_DEVICE_NAME = "device.name";

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter mPairedDevicesArrayAdapter;
    private ListView mPairedDevicesListView;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) { //device doesn't support bluetooth
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) { //request bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mPairedDevicesListView = (ListView) findViewById(R.id.pairedDevicesList);
        mPairedDevicesListView.setOnItemClickListener(mDeviceClickListener);

        searchPairedDevices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if(mBluetoothAdapter == null) { //device doesn't support bluetooth
                    return;
                }
                searchPairedDevices();
                break;
        }
    }

    private void searchPairedDevices() {
        mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if(mPairedDevices.size() > 0) {
            for (BluetoothDevice device : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        mPairedDevicesListView.setAdapter(mPairedDevicesArrayAdapter);
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            String info[] = (((TextView) v).getText().toString()).split("\n");

            String name = info[0];
            String address = info[1];

            Intent intent = new Intent(getApplicationContext() , EditDeviceBehaviorActivity.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            intent.putExtra(EXTRA_DEVICE_NAME, name);
            startActivity(intent);
        }
    };
}
