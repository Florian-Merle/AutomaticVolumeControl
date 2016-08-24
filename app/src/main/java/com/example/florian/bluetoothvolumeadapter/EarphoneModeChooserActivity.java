package com.example.florian.bluetoothvolumeadapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.florian.bluetoothvolumeadapter.Database.EarphoneModeDAO;
import com.example.florian.bluetoothvolumeadapter.Database.EarphoneModeOptions;

import java.util.List;

/**
 * Created by Florian on 24/08/2016.
 */
public class EarphoneModeChooserActivity extends Activity {
    private EarphoneModeDAO mEarphoneModeDAO;
    private AudioManager mAudioManager;
    private ListView mEarphoneModesListView;
    private List<EarphoneModeOptions> mEarphoneModeList;
    private ArrayAdapter<Object> mEarphoneModesArrayAdapter;

    //TODO when closing activity, it doesn't get back to the right activity

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earphone_mode_chooser);

        mEarphoneModeDAO = new EarphoneModeDAO(this);
        mEarphoneModeDAO.open();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mEarphoneModesListView = (ListView) findViewById(R.id.earphoneModesList);
        mEarphoneModesListView.setOnItemClickListener(mEarphoneModeClickListener);

        updateListView();
    }

    private AdapterView.OnItemClickListener mEarphoneModeClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            String info[] = (((TextView) v).getText().toString()).split("\n");

            String name = info[0];

            EarphoneModeOptions emo = mEarphoneModeDAO.select(name);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EarphoneModeChooserActivity.this);

            int flag = 0;
            if(prefs.getBoolean("show_am_ui", false)) {
                flag = AudioManager.FLAG_SHOW_UI;
            }

            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    emo.getVolume(),
                    flag);

            finish();
        }
    };

    private void updateListView() {
        mEarphoneModeList = mEarphoneModeDAO.selectAll();

        mEarphoneModesArrayAdapter = new ArrayAdapter<>(this, R.layout.earphone_mode_name);
        for(EarphoneModeOptions emo : mEarphoneModeList) {
            int volume = (emo.getVolume() * 100) / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mEarphoneModesArrayAdapter.add(emo.getName() + "\n" + getResources().getString(R.string.volume)+ ": " + volume + "%");
        }

        mEarphoneModesListView.setAdapter(mEarphoneModesArrayAdapter);
    }
}
