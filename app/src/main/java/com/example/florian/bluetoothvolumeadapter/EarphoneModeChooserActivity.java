package com.example.florian.bluetoothvolumeadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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
public class EarphoneModeChooserActivity extends ActionBarActivity {
    private EarphoneModeDAO mEarphoneModeDAO;
    private AudioManager mAudioManager;
    private ListView mEarphoneModesListView;
    private List<EarphoneModeOptions> mEarphoneModeList;
    private ArrayAdapter<Object> mEarphoneModesArrayAdapter;
    private Toolbar mToolbar;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earphone_mode_chooser);

        mEarphoneModeDAO = new EarphoneModeDAO(this);
        mEarphoneModeDAO.open();

        /* tool bar */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(R.string.choose_earphone_mode);

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

            stop();
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

    private void stop() {
        this.finishAffinity();
    }

    public void action_edit_modes(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tab", 1);
        startActivity(intent);
    }
}
