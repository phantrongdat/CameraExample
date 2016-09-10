package com.dat17.cameraexample;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnOpenCamera, btnSave, btnPick;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView)findViewById(R.id.imageView);
        btnOpenCamera=(Button)findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });

        btnSave=(Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        btnPick=(Button) findViewById(R.id.btnPick);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        });
    }


    public void saveImage() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String date = sdf.format(new Date());

        String name = "IMG_" + date + ".jpg";

        File filePath = Environment.getExternalStorageDirectory();
        File dir = new File(filePath.getAbsolutePath().toString() + "/CameraExample");
        dir.mkdirs();

        Bitmap imgBitmap=((BitmapDrawable) imageView.getDrawable()).getBitmap();

        File file = new File(dir, name);

        try {
            OutputStream outputStream = new FileOutputStream(file);
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(MainActivity.this,filePath.getAbsolutePath()+dir.getName()+"/"+file.getName(),Toast.LENGTH_LONG).show();
            //MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode==1) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(bitmap);
        }
        if (resultCode==RESULT_OK && requestCode==2) {
            Uri uri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String imgPath = cursor.getString(columnIndex);

            File file = new File(imgPath);
            try {
                InputStream inputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
