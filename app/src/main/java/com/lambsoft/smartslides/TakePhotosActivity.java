package com.lambsoft.smartslides;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePhotosActivity extends Activity {
    Button tirarFoto;
    Button salvar;

    TextView titleAula;

    boolean pTaken;

    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo_activity_layout);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        pTaken = false;

        helper = new DatabaseHelper(this);

        titleAula = (TextView) findViewById(R.id.tirar_fotos_text);
        tirarFoto = (Button) findViewById(R.id.bTirarFotosAula);
        salvar = (Button) findViewById(R.id.bSalvarAula);

        Intent intent = getIntent();
        titleAula.setText("Aula " + intent.getStringExtra("aulaid"));
    }

    public void onTirarFotosAulaButtonClick(View view){
        dispatchTakePictureIntent();
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.lambsoft.smartslides.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if(resultCode == RESULT_OK){
                SQLiteDatabase db;

                ContentValues values = new ContentValues();
                values.put("path", currentPhotoPath);
                values.put("aula_id", getIntent().getStringExtra("aulaid"));
                values.put("cadeira_id", getIntent().getStringExtra("cadeiraid"));

                db = helper.getWritableDatabase();
                db.insert("slide", null, values);

                pTaken = true;
                salvar.setBackgroundResource(R.drawable.but_enable);
                Toast.makeText(this, "Foto Capturada", Toast.LENGTH_SHORT).show();

                File f = new File(currentPhotoPath);
                Bitmap  b = BitmapFactory.decodeFile(currentPhotoPath);
                Bitmap out = Bitmap.createScaledBitmap(b, 480, 270, false);

                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(f);
                    out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    b.recycle();
                    out.recycle();
                } catch (Exception e) {}
            }
            else{
                File file = new File(currentPhotoPath);
                boolean deleted = file.delete();
                if(!deleted){
                    Toast.makeText(this, "Failed to delete empty file", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void onSalvarAula(View view){
        if(pTaken) finish();
    }
}
