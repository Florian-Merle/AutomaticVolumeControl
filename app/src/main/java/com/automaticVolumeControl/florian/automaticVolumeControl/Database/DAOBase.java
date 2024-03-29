package com.automaticVolumeControl.florian.automaticVolumeControl.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DAOBase {
  protected final static int VERSION = 1;
  protected final static String DATABASE_NAME = "database.db";

  protected SQLiteDatabase mDb = null;
  protected DatabaseHandler mHandler = null;
    
  public DAOBase(Context pContext) {
    this.mHandler = new DatabaseHandler(pContext, DATABASE_NAME, null, VERSION);
  }

  public SQLiteDatabase open() {
    mDb = mHandler.getWritableDatabase();
    return mDb;
  }

  public void close() {
    mDb.close();
  }

  public SQLiteDatabase getDb() {
    return mDb;
  }
}
