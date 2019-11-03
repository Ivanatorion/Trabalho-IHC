package com.lambsoft.smartslides;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class DisplaySlidesActivity extends ListActivity implements AdapterView.OnItemClickListener {

    String[] slidesPaths;

    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DatabaseHelper(this);

        String aulaId = getIntent().getExtras().getString("id");

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, path FROM slide WHERE aula_id = ?", new String[] { aulaId });
        cursor.moveToFirst();

        slidesPaths = new String[cursor.getCount()];

        for(int i = 0; i < cursor.getCount(); i++){
            String path = cursor.getString(1);

            slidesPaths[i] = path;

            cursor.moveToNext();
        }
        cursor.close();

        ListView listView = getListView();
        listView.setOnItemClickListener(this);

        setListAdapter(new MyAdapterSlides(this, slidesPaths));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0) return;


    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}
