package com.lambsoft.smartslides;

import android.app.AlertDialog;
import android.app.ListActivity;
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

public class CadeirasListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private DatabaseHelper helper;

    private String[] cadeiras;
    String[] cadeiras_id;
    int initialCount, deleteSinceStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DatabaseHelper(this);

        deleteSinceStart = 0;

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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
                if(position == 0) return true;
                new AlertDialog.Builder(CadeirasListActivity.this)
                        .setTitle(R.string.delete_cadeira)
                        .setMessage(R.string.confirm_delete_cadeira)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCadeira(position-1);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });


    }

    private void deleteCadeira(int position){

        ArrayList<String> slidesPaths = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, path FROM slide WHERE cadeira_id = ?", new String[] { cadeiras_id[position] });
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            String path = cursor.getString(1);

            slidesPaths.add(path);

            cursor.moveToNext();
        }
        cursor.close();
        for(String path : slidesPaths){
            File f = new File(path);
            f.delete();
        }

        db = helper.getWritableDatabase();
        db.delete("slide", "cadeira_id = ?", new String[] {cadeiras_id[position]});
        db.delete("aula", "cadeira_id = ?", new String[] {cadeiras_id[position]});
        db.delete("cadeira", "_id = ?", new String[] {cadeiras_id[position]});
        deleteSinceStart++;
        for(int i = position; i < initialCount-deleteSinceStart; i++){
            cadeiras[i] = cadeiras[i+1];
            cadeiras_id[i] = cadeiras_id[i+1];
        }
        setListAdapter(new MyAdapterCadeiras(this, listarCadeiras()));
        getListView().invalidateViews();

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

        Intent intent;
        intent = new Intent(this, AulaListActivity.class);
        intent.putExtra("id", cadeiras_id[position-1]);
        intent.putExtra("cname", cadeiras[position-1]);

        Log.d("Before activity", "Before");
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