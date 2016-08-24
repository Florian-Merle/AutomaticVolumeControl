package com.example.florian.bluetoothvolumeadapter;

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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.florian.bluetoothvolumeadapter.Database.EarphoneModeDAO;
import com.example.florian.bluetoothvolumeadapter.Database.EarphoneModeOptions;

import java.util.List;

/**
 * Created by Florian on 24/08/2016.
 */
public class earphoneProfilesListFragment extends Fragment {
    private EarphoneModeDAO mEarphoneModeDAO;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earphone_profiles_list, container, false);

        mEarphoneModeDAO = new EarphoneModeDAO(getContext());
        mEarphoneModeDAO.openEarphoneModes();

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        List<EarphoneModeOptions> list = mEarphoneModeDAO.selectAll();

        for (EarphoneModeOptions emo : list) {
            Toast.makeText(getContext(), emo.getName() + " " + emo.getVolume(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public void action_new_earphone_mode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.earphone_title));

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_earphone_profile_new, null);

        final EditText editText = (EditText) v.findViewById(R.id.earphoneName);

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
        }
    }
}