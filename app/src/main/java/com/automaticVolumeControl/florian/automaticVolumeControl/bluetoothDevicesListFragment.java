package com.automaticVolumeControl.florian.automaticVolumeControl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.automaticVolumeControl.florian.automaticVolumeControl.Database.DeviceDAO;
import com.automaticVolumeControl.florian.automaticVolumeControl.Database.DeviceOptions;

import java.util.Set;

/**
 * Created by Florian on 24/08/2016.
 */
public class bluetoothDevicesListFragment extends Fragment {
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter mPairedDevicesArrayAdapter;
    private ListView mPairedDevicesListView;
    private DeviceDAO mDeviceDAO;
    private DeviceOptions mDevice;
    private int mDefaultVolumeValue = 0;

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

            action_update_bt_device(address, name);
        }
    };

    private void action_update_bt_device(String address, String name) {
        mDeviceDAO = new DeviceDAO(getContext());
        mDeviceDAO.open();

        mDevice = mDeviceDAO.select(address);
        if(mDevice == null) { // create device in database
            mDevice = new DeviceOptions(address, name);
            mDevice.setVolume(mDefaultVolumeValue);
            mDeviceDAO.insert(mDevice);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.edb_title));

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_device_behavior, null);

        final Switch activateDevice = (Switch) v.findViewById(R.id.activateDevice);
        activateDevice.setChecked(mDevice.getActivated() != 0);
        final Switch rememberVolume = (Switch) v.findViewById(R.id.rememberVolume);
        rememberVolume.setChecked(mDevice.getRememberLastVolume() != 0);
        final SeekBar seekbar = (SeekBar) v.findViewById(R.id.volumeSeekBar);
        seekbar.setProgress(mDevice.getVolume());

        if(mDevice.getActivated() == 0) {
            rememberVolume.setEnabled(false);
            seekbar.setEnabled(false);
        }

        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        seekbar.setMax(max);
        seekbar.setProgress(mDevice.getVolume());

        activateDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                rememberVolume.setEnabled(b);
                seekbar.setEnabled(b);
            }
        });

        builder.setView(v);

        builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Boolean activateDeviceValue = activateDevice.isChecked();
                Boolean rememberVolumeValue = rememberVolume.isChecked();
                int volume = seekbar.getProgress();

                update_device(activateDeviceValue, rememberVolumeValue, volume);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    private void update_device(Boolean activateDeviceValue, Boolean rememberVolumeValue, int volume) {
        mDevice.setActivated((activateDeviceValue) ? 1 : 0);
        mDevice.setRememberLastVolume((rememberVolumeValue) ? 1 : 0);
        mDevice.setVolume(volume);
        mDeviceDAO.update(mDevice);
    }

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
