package com.lambsoft.smartslides;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button takePhotosButton;
    Button viewSlidesButton;

    boolean haveCadeira;
    boolean haveAula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotosButton = (Button) findViewById(R.id.bTakePhotos);
        viewSlidesButton = (Button) findViewById(R.id.bViewSlides);
    }

    @Override
    protected void onResume(){
        super.onResume();

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, name FROM cadeira", null);
        if(cursor.getCount() == 0){
            takePhotosButton.setBackgroundResource(R.drawable.but_disable);
            viewSlidesButton.setBackgroundResource(R.drawable.but_disable);
            haveAula = false;
            haveCadeira = false;
            cursor.close();
        }
        else{
            takePhotosButton.setBackgroundResource(R.drawable.but_enable);
            haveCadeira = true;
            cursor.close();
            cursor = db.rawQuery("SELECT _id, name FROM aula", null);
            if(cursor.getCount() == 0){
                viewSlidesButton.setBackgroundResource(R.drawable.but_disable);
                haveAula = false;
            }
            else{
                viewSlidesButton.setBackgroundResource(R.drawable.but_enable);
                haveAula = true;
            }
            cursor.close();
        }

        helper.close();
    }

    public void onTakePhotosButtonClick(View view) {
        if(haveCadeira)
            startActivity(new Intent(this, TakePhotoCadeiraListActivity.class));
    }

    public void onViewSlidesButtonClick(View view){
        if(haveAula)
            startActivity(new Intent(this, CadeirasListActivity.class));
    }

    public void onAddCadeira(View view){
        startActivity(new Intent(this, NovaCadeiraActivity.class));
    }
}
