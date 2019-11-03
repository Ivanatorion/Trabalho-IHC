package com.lambsoft.smartslides;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NovaCadeiraActivity extends Activity {

    EditText cname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_cadeira_screen);

        cname = (EditText) findViewById(R.id.nome_cadeira);
    }

    public void onConfirm(View view){
        ContentValues values = new ContentValues();

        values.put("name", cname.getText().toString());

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        db.insert("cadeira", null, values);

        finish();
    }
}


