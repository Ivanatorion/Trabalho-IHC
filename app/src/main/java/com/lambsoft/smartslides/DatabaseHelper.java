package com.lambsoft.smartslides;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String BANCO_DADOS = "YTData";
    private static int version = 1;

    public DatabaseHelper(Context context){
        super(context, BANCO_DADOS, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE cadeira(_id INTEGER PRIMARY KEY, name TEXT);");

        db.execSQL("CREATE TABLE aula(_id INTEGER PRIMARY KEY, name TEXT, cadeira_id INTEGER, FOREIGN KEY(cadeira_id) REFERENCES cadeira(_id));");

        db.execSQL("CREATE TABLE slide(_id INTEGER PRIMARY KEY, path TEXT, aula_id INTEGER, cadeira_id INTEGER, FOREIGN KEY(aula_id) REFERENCES aula(_id), FOREIGN KEY(cadeira_id) REFERENCES cadeira(_id));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
