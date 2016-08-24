package com.example.florian.bluetoothvolumeadapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by Florian on 24/08/2016.
 */
public class bluetoothDevicesListFragment extends Fragment {
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter mPairedDevicesArrayAdapter;
    private ListView mPairedDevicesListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_devices_list, container, false);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mPairedDevicesListView = (ListView) view.findViewById(R.id.pairedDevicesList);
        mPairedDevicesListView.setOnItemClickListener(mDeviceClickListener);

        searchPairedDevices();

        return view;
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            String info[] = (((TextView) v).getText().toString()).split("\n");

            String name = info[0];
            String address = info[1];

            Intent intent = new Intent(getContext() , EditDeviceBehaviorActivity.class);
            intent.putExtra(MainActivity.EXTRA_DEVICE_ADDRESS, address);
            intent.putExtra(MainActivity.EXTRA_DEVICE_NAME, name);
            startActivity(intent);
        }
    };

    public void searchPairedDevices() {
        mPairedDevicesArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.device_name);

        mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if(mPairedDevices.size() > 0) {
            for (BluetoothDevice device : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        mPairedDevicesListView.setAdapter(mPairedDevicesArrayAdapter);
    }
}
