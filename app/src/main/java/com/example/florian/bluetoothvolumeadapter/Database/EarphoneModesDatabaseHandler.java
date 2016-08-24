package com.example.florian.bluetoothvolumeadapter.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Florian on 24/08/2016.
 */
public class EarphoneModesDatabaseHandler extends SQLiteOpenHelper {
    public static final String EARPHONE_MODE_NAME = "name";
    public static final String EARPHONE_MODE_VOLUME = "volume";

    public static final String EARPHONE_MODE_TABLE_NAME = "Earphone_Mode";
    public static final String EARPHONE_MODE_TABLE_CREATE =
            "CREATE TABLE " + EARPHONE_MODE_TABLE_NAME +" (" +
                    EARPHONE_MODE_NAME + " STRING PRIMARY KEY," +
                    EARPHONE_MODE_VOLUME + " INTEGER);";

    public static final String EARPHONE_MODE_TABLE_DROP = "DROP TABLE IF EXISTS " + EARPHONE_MODE_TABLE_NAME + ";";

    public EarphoneModesDatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EARPHONE_MODE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(EARPHONE_MODE_TABLE_DROP);
        onCreate(db);
    }
}