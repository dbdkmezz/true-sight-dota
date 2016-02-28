package com.carver.paul.dotavision.Models;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "advantages.db";
    private static final int DATABASE_VERSION = 4;

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

/*    public Cursor getEmployees() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"0 _id", "FirstName", "LastName"};
        String sqlTables = "Employees";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null,
                null, null, null);

        c.moveToFirst();
        return c;

    }*/

}

/*
package com.carver.paul.dotavision.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "advantages.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getHeroes() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"_id", "name"};
        String sqlTables = "Heroes";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null,
                null, null, null);

        c.moveToFirst();
        return c;

    }
}*/
