package com.lambsoft.smartslides;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakePhotoCadeiraListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private DatabaseHelper helper;

    private String[] cadeiras;
    String[] cadeiras_id;
    int initialCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DatabaseHelper(this);

        TextView tv = new TextView(getApplicationContext());

        String title = getResources().getString(R.string.word_cadeiras);

        tv.setText(title);
        tv.setTextSize(32);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(Color.parseColor("#F2C983"));

        ListView listView = getListView();
        listView.addHeaderView(tv);
        listView.setOnItemClickListener(this);

        setListAdapter(new MyAdapterCadeiras(this, listarCadeiras()));


    }

    private String[] listarCadeiras() {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, name FROM cadeira", null);

        cursor.moveToFirst();

        initialCount = cursor.getCount();

        cadeiras = new String[initialCount];

        cadeiras_id = new String[initialCount];

        for(int i = 0; i < cursor.getCount(); i++){

            cadeiras_id[i] = (cursor.getString(0));
            String name = cursor.getString(1);

            cadeiras[i] = name;

            cursor.moveToNext();
        }
        cursor.close();

        return cadeiras;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) return;

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, name FROM aula WHERE cadeira_id = ?", new String[] { cadeiras_id[position-1] });
        cursor.moveToFirst();

        int aulaid;

        if(cursor.isBeforeFirst()) {
            aulaid = 1;
            Log.d("0 Aulas", "F");
        }
        else {
            Log.d("0 Aulas", "D");
            cursor.moveToLast();
            aulaid = cursor.getInt(0);
            aulaid = aulaid + 1;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("name", "Aula " + aulaid);
        values.put("cadeira_id", cadeiras_id[position-1]);

        db = helper.getWritableDatabase();
        db.insert("aula", null, values);

        /*
        db = helper.getReadableDatabase();

        cursor = db.rawQuery("SELECT _id, name FROM aula WHERE cadeira_id = ?", new String[] { cadeiras_id[position-1] });

        cursor.moveToLast();

        Toast.makeText(this, "ID: " + cursor.getInt(0), Toast.LENGTH_LONG).show();

        cursor.close();
        */

        Intent intent;
        intent = new Intent(this, TakePhotosActivity.class);
        intent.putExtra("aulaid", Integer.toString(aulaid));
        intent.putExtra("cadeiraid", cadeiras_id[position-1]);

        Log.d("Before Take Photo", "Before");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListAdapter(new MyAdapterCadeiras(this, listarCadeiras()));
        getListView().invalidateViews();
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}