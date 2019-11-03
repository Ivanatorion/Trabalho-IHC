package com.lambsoft.smartslides;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MyAdapterSlides extends ArrayAdapter<String> {

    public MyAdapterSlides(Context context, String[] values){
        super(context, R.layout.display_slides_activity, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater theInflator = LayoutInflater.from(getContext());

        View theView = theInflator.inflate(R.layout.display_slides_activity, parent, false);

        ImageView imv = (ImageView) theView.findViewById(R.id.slide);

        String SlidePath = getItem(position);

        Bitmap myBitmap = BitmapFactory.decodeFile(SlidePath);
        imv.setImageBitmap(myBitmap);

        return theView;

    }
}
