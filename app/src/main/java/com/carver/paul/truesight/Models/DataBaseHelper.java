package com.carver.paul.truesight.Models;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "advantages.db";
    private static final int DATABASE_VERSION = 13;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // setForcedUpgrade is needed to enable updating database version updates without an upgrade
        // script (explained here: https://github.com/jgilfelt/android-sqlite-asset-helper )
        setForcedUpgrade();

        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

    }
}