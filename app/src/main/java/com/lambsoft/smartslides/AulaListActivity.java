package com.lambsoft.smartslides;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import android.net.Uri;

public class AulaListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private DatabaseHelper helper;

    String cadeira_id;
    String[] aulas;
    String[] aulas_id;
    int initialCount, deleteSinceStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Gonna Start data list", "Starting datalst");

        helper = new DatabaseHelper(this);

        deleteSinceStart = 0;

        Intent intent = getIntent();
        cadeira_id = intent.getStringExtra("id");

        TextView tv = new TextView(getApplicationContext());

        String title = getResources().getString(R.string.aulas_de)  + " " + intent.getStringExtra("cname");

        tv.setText(title);
        tv.setTextSize(36);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(Color.parseColor("#F2C983"));

        aulas = listarAulas();

        ListView listView = getListView();
        listView.addHeaderView(tv);
        listView.setOnItemClickListener(this);

        setListAdapter(new MyAdapterCadeiras(this, aulas));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
                if(position == 0) return true;

                new AlertDialog.Builder(AulaListActivity.this)
                        .setTitle(R.string.delete_aula)
                        .setMessage(R.string.confirm_delete_aula)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAula(position-1);
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

    private void deleteAula(int position){

        ArrayList<String> slidesPaths = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, path FROM slide WHERE aula_id = ?", new String[] { aulas_id[position] });
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
        db.delete("slide", "aula_id = ?", new String[] {aulas_id[position]});
        db.delete("aula", "_id = ?", new String[] {aulas_id[position]});
        deleteSinceStart++;
        for(int i = position; i < initialCount-deleteSinceStart; i++){
            aulas[i] = aulas[i+1];
            aulas_id[i] = aulas_id[i+1];
        }
        setListAdapter(new MyAdapterCadeiras(this, listarAulas()));
        getListView().invalidateViews();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0) return;

        Intent intent;
        intent = new Intent(this, DisplaySlidesActivity.class);
        intent.putExtra("id", aulas_id[position-1]);

        startActivity(intent);
    }

    private String[] listarAulas(){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, name FROM aula WHERE cadeira_id = ?", new String[] { cadeira_id });

        initialCount = cursor.getCount();

        aulas = new String[initialCount];

        aulas_id = new String[initialCount];

        cursor.moveToFirst();

        String[] aulas = new String[cursor.getCount()];

        for(int i = 0; i < cursor.getCount(); i++){
            String id = cursor.getString(0);
            String name = cursor.getString(1);

            aulas[i] = name;
            aulas_id[i] = id;

            cursor.moveToNext();
        }
        cursor.close();

        return aulas;
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }


}
