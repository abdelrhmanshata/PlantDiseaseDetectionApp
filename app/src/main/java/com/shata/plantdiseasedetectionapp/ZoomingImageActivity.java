package com.shata.plantdiseasedetectionapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ZoomingImageActivity extends AppCompatActivity {


    PhotoView photoView;

    String Uri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zooming_image);

        photoView = findViewById(R.id.photo_view);

        Uri = getIntent().getStringExtra("ImageUri");

        try {
            Picasso
                    .get()
                    .load(Uri)
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(photoView);
        } catch (Exception ignored) {
        }


    }
}