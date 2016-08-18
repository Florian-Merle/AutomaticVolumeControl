package com.example.florian.bluetoothvolumeadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Florian on 11/08/2016.
 */
public class DeviceDAO extends DAOBase{
    public static final String DEVICE_TABLE_NAME = "Device";

    public DeviceDAO(Context pContext) {
        super(pContext);
    }

    public long insert(DeviceOptions d) {
        ContentValues values = new ContentValues();

        values.put(DevicesDatabaseHandler.DEVICE_ADRESS, d.getAdresse());
        values.put(DevicesDatabaseHandler.DEVICE_NAME, d.getName());
        values.put(DevicesDatabaseHandler.DEVICE_ACTIVATED, d.getActivated());
        values.put(DevicesDatabaseHandler.DEVICE_REMEMBER_LAST_VOLUME, d.getRememberLastVolume());
        values.put(DevicesDatabaseHandler.DEVICE_VOLUME, d.getVolume());

        return mDb.insert(DevicesDatabaseHandler.DEVICE_TABLE_NAME, null, values);
    }

    public void delete(String id) {
        mDb.delete(DevicesDatabaseHandler.DEVICE_TABLE_NAME, DevicesDatabaseHandler.DEVICE_ADRESS + " = ?", new String[] {String.valueOf(id)});
    }

    public int update(DeviceOptions d) {
        ContentValues values = new ContentValues();

        values.put(DevicesDatabaseHandler.DEVICE_ADRESS, d.getAdresse());
        values.put(DevicesDatabaseHandler.DEVICE_NAME, d.getName());
        values.put(DevicesDatabaseHandler.DEVICE_ACTIVATED, d.getActivated());
        values.put(DevicesDatabaseHandler.DEVICE_REMEMBER_LAST_VOLUME, d.getRememberLastVolume());
        values.put(DevicesDatabaseHandler.DEVICE_VOLUME, d.getVolume());

        return mDb.update(DevicesDatabaseHandler.DEVICE_TABLE_NAME, values, DevicesDatabaseHandler.DEVICE_ADRESS + " = " +d.getAdresse(), null);
    }

    public DeviceOptions select(String id) {
        Cursor c = mDb.query(DevicesDatabaseHandler.DEVICE_TABLE_NAME,
                new String[] {DevicesDatabaseHandler.DEVICE_ADRESS,
                              DevicesDatabaseHandler.DEVICE_NAME,
                              DevicesDatabaseHandler.DEVICE_ACTIVATED,
                              DevicesDatabaseHandler.DEVICE_REMEMBER_LAST_VOLUME,
                              DevicesDatabaseHandler.DEVICE_VOLUME},
                DevicesDatabaseHandler.DEVICE_ADRESS+ " LIKE \"" + id +"\"",
                null, null, null, null);
        return cursorToDeviceOptions(c);
    }

    private DeviceOptions cursorToDeviceOptions(Cursor c){
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();

        DeviceOptions d = new DeviceOptions(c.getString(1), c.getString(2));
        d.setVolume(c.getInt(5));
        d.setActivated(c.getInt(3));
        d.setRememberLastVolume(c.getInt(4));

        c.close();
        return d;
    }
}

