package com.automaticVolumeControl.florian.automaticVolumeControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.automaticVolumeControl.florian.automaticVolumeControl.Database.EarphoneModeDAO;
import com.automaticVolumeControl.florian.automaticVolumeControl.Database.EarphoneModeOptions;

import java.util.List;

/**
 * Created by Florian on 24/08/2016.
 */
public class earphoneProfilesListFragment extends Fragment {
    private EarphoneModeDAO mEarphoneModeDAO;
    private AudioManager mAudioManager;
    private List<EarphoneModeOptions> mEarphoneModeList;
    private ListView mEarphoneModesListView;
    private ArrayAdapter<Object> mEarphoneModesArrayAdapter;
    private Switch mEarphoneModesActivatedSwitch;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean mEarphoneModesNeedNotification = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earphone_profiles_list, container, false);

        mEarphoneModeDAO = new EarphoneModeDAO(getContext());
        mEarphoneModeDAO.open();

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        mEarphoneModesListView = (ListView) view.findViewById(R.id.earphoneModesList);
        mEarphoneModesListView.setOnItemClickListener(mEarphoneModeClickListener);

        mEarphoneModesActivatedSwitch = (Switch) view.findViewById(R.id.switchEarphoneModes);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Boolean v = mSharedPreferences.getBoolean("earphones_modes_activated", false);
        mEarphoneModesActivatedSwitch.setChecked(v);

        mEarphoneModesActivatedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mEditor = mSharedPreferences.edit();
                mEditor.putBoolean("earphones_modes_activated",b);
                mEditor.commit();

                if (b && mEarphoneModesNeedNotification) {
                    EarphoneModeChooserNotificationManager.createNotification(getContext());
                }
                else {
                    EarphoneModeChooserNotificationManager.deleteNotification(getContext());
                }
            }
        });


        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        getContext().registerReceiver(mReceiver, filter);

        updateListView();

        return view;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(Intent.ACTION_HEADSET_PLUG.equals(action)) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Boolean v = sharedPreferences.getBoolean("earphones_modes_activated", false);
                if(v) {
                    int state = intent.getIntExtra("state", -1);
                    if(state >= 1) {
                        mEarphoneModeDAO = new EarphoneModeDAO(context);
                        mEarphoneModeDAO.open();

                        List<EarphoneModeOptions> result = mEarphoneModeDAO.selectAll();

                        if (result.size() != 0) {
                            mEarphoneModesNeedNotification = true;
                        }
                    }
                    else {
                        mEarphoneModesNeedNotification = false;
                    }
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mEarphoneModeClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            String info[] = (((TextView) v).getText().toString()).split("\n");

            String name = info[0];

            action_update_earphone_mode(name);
        }
    };

    public void action_update_earphone_mode(String name) {
        final EarphoneModeOptions emo = mEarphoneModeDAO.select(name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.earphone_title_edit));

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_earphone_profile_new, null);

        final EditText editText = (EditText) v.findViewById(R.id.earphoneName);
        editText.setText(emo.getName());
        editText.setSelection(editText.length());

        final SeekBar seekbar = (SeekBar) v.findViewById(R.id.earphoneVolume);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar.setMax(max);
        seekbar.setProgress(emo.getVolume());

        builder.setView(v);

        builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                int volume = seekbar.getProgress();

                update_mode(emo.getName(), name, volume);
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete_mode(emo.getName());
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

    public void action_new_earphone_mode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.earphone_title_new));

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_earphone_profile_new, null);

        final EditText editText = (EditText) v.findViewById(R.id.earphoneName);
        editText.setSelection(editText.length());

        final SeekBar seekbar = (SeekBar) v.findViewById(R.id.earphoneVolume);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar.setMax(max);

        builder.setView(v);

        builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                int volume = seekbar.getProgress();

                new_mode(name, volume);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void new_mode(String n, int v) {
        if (mEarphoneModeDAO.select(n) != null || n == "") {
            Toast.makeText(getContext(), getResources().getString(R.string.earphone_problem),Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            EarphoneModeOptions emo = new EarphoneModeOptions(n, v);
            mEarphoneModeDAO.insert(emo);
            updateListView();
        }
    }

    private void update_mode(String lastName, String name, int volume) {
        EarphoneModeOptions emo = new EarphoneModeOptions(name, volume);
        if (!mEarphoneModeDAO.update(lastName, emo)) {
            Toast.makeText(getContext(), getResources().getString(R.string.earphone_problem),Toast.LENGTH_SHORT).show();
        }
        updateListView();
    }

    private void updateListView() {
        mEarphoneModeList = mEarphoneModeDAO.selectAll();

        mEarphoneModesArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.earphone_mode_name);
        for(EarphoneModeOptions emo : mEarphoneModeList) {
            int volume = (emo.getVolume() * 100) / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mEarphoneModesArrayAdapter.add(emo.getName() + "\n" + getResources().getString(R.string.volume)+ ": " + volume + "%");
        }

        mEarphoneModesListView.setAdapter(mEarphoneModesArrayAdapter);
    }

    private void delete_mode(final String name) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_warning)
                .setTitle(getResources().getString(R.string.delete))
                .setMessage(getResources().getString(R.string.confirm_delete_earphone_mode))
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEarphoneModeDAO.delete(name);
                        updateListView();
                    }

                })
                .setNegativeButton(getResources().getString(android.R.string.no), null)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getContext().unregisterReceiver(mReceiver);
    }
}