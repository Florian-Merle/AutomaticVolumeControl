package com.example.florian.bluetoothvolumeadapter.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian on 24/08/2016.
 */
public class EarphoneModeDAO extends DAOBase {
    public static final String EARPHONE_MODE_TABLE_NAME = "Earphone_Mode";

    public EarphoneModeDAO(Context pContext) {
        super(pContext);
    }

    public long insert(EarphoneModeOptions d) {
        ContentValues values = new ContentValues();

        values.put(EarphoneModesDatabaseHandler.EARPHONE_MODE_NAME, d.getName());
        values.put(EarphoneModesDatabaseHandler.EARPHONE_MODE_VOLUME, d.getVolume());

        return mDb.insert(EarphoneModesDatabaseHandler.EARPHONE_MODE_TABLE_NAME, null, values);
    }

    public void delete(String id) {
        mDb.delete(EarphoneModesDatabaseHandler.EARPHONE_MODE_TABLE_NAME, EarphoneModesDatabaseHandler.EARPHONE_MODE_NAME + " = ?", new String[] {String.valueOf(id)});
    }

    public Boolean update(String id, EarphoneModeOptions d) {
        ContentValues values = new ContentValues();

        values.put(EarphoneModesDatabaseHandler.EARPHONE_MODE_NAME, d.getName());
        values.put(EarphoneModesDatabaseHandler.EARPHONE_MODE_VOLUME, d.getVolume());

        String where = EarphoneModesDatabaseHandler.EARPHONE_MODE_NAME + " = ?";
        String[] whereArgs = {id };

        try {
            mDb.update(EarphoneModesDatabaseHandler.EARPHONE_MODE_TABLE_NAME,
                    values,
                    where,
                    whereArgs);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public EarphoneModeOptions select(String id) {
        Cursor c = mDb.query(EarphoneModesDatabaseHandler.EARPHONE_MODE_TABLE_NAME,
                new String[] {EarphoneModesDatabaseHandler.EARPHONE_MODE_NAME,
                        EarphoneModesDatabaseHandler.EARPHONE_MODE_VOLUME},
                EarphoneModesDatabaseHandler.EARPHONE_MODE_NAME+ " LIKE \"" + id +"\"",
                null, null, null, null);
        return cursorToEarphoneModeOptions(c);
    }

    public List<EarphoneModeOptions> selectAll() {
        List<EarphoneModeOptions> list = new ArrayList<EarphoneModeOptions>();

        Cursor c = mDb.rawQuery("SELECT * FROM "+EarphoneModesDatabaseHandler.EARPHONE_MODE_TABLE_NAME, null);

        c.moveToFirst();

        while (c.isAfterLast() == false) {
            list.add(new EarphoneModeOptions(c.getString(0), c.getInt(1)));
            c.moveToNext();
        }

        return list;
    }

    private EarphoneModeOptions cursorToEarphoneModeOptions(Cursor c){
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();

        EarphoneModeOptions d = new EarphoneModeOptions(c.getString(0), c.getInt(1));

        c.close();
        return d;
    }
}