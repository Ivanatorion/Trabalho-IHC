package com.lambsoft.smartslides;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTakePhotosButtonClick(View view) {

        startActivity(new Intent(this, TakePhotoCadeiraListActivity.class));
    }

    public void onViewSlidesButtonClick(View view){
        startActivity(new Intent(this, CadeirasListActivity.class));
    }

    public void onAddCadeira(View view){
        startActivity(new Intent(this, NovaCadeiraActivity.class));
    }
}
