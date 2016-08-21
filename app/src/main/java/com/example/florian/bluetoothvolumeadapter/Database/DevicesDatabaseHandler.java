package com.example.florian.bluetoothvolumeadapter.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Florian on 11/08/2016.
 */
public class DevicesDatabaseHandler extends SQLiteOpenHelper {
    public static final String DEVICE_ADRESS = "adress";
    public static final String DEVICE_NAME = "name";
    public static final String DEVICE_ACTIVATED = "activated";
    public static final String DEVICE_REMEMBER_LAST_VOLUME = "remember_last_volume";
    public static final String DEVICE_VOLUME = "volume";

    public static final String DEVICE_TABLE_NAME = "Device";
    public static final String DEVICE_TABLE_CREATE =
                    "CREATE TABLE " + DEVICE_TABLE_NAME +" (" +
                    DEVICE_ADRESS + " STRING PRIMARY KEY, " +
                    DEVICE_NAME + " STRING," +
                    DEVICE_ACTIVATED + " INTEGER, " +
                    DEVICE_REMEMBER_LAST_VOLUME + " INTEGER, " +
                    DEVICE_VOLUME + " INTEGER);";

    public static final String DEVICE_TABLE_DROP = "DROP TABLE IF EXISTS " + DEVICE_TABLE_NAME + ";";

    public DevicesDatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DEVICE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DEVICE_TABLE_DROP);
        onCreate(db);
    }
}
