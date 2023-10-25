package com.shata.plantdiseasedetectionapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shata.plantdiseasedetectionapp.Adapter.Adapter_All_Photos;
import com.shata.plantdiseasedetectionapp.Class.ModelImageBanner;
import com.shata.plantdiseasedetectionapp.databinding.ActivityBannerBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BannerActivity extends AppCompatActivity implements Adapter_All_Photos.OnItemClickListener {

    ActivityBannerBinding bannerBinding;
    Adapter_All_Photos adapterAllPhotos;
    List<ModelImageBanner> mAllImageBanner;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refBannerImage = database.getReference("BannerImages");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("BannerImages");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bannerBinding = ActivityBannerBinding.inflate(getLayoutInflater());
        setContentView(bannerBinding.getRoot());

        // Permissions to open Camera
        ActivityCompat.requestPermissions(Objects.requireNonNull(BannerActivity.this),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        bannerBinding.bannerRecyclerview.setHasFixedSize(true);
        bannerBinding.bannerRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        mAllImageBanner = new ArrayList<>();

        adapterAllPhotos = new Adapter_All_Photos(this, mAllImageBanner);
        bannerBinding.bannerRecyclerview.setAdapter(adapterAllPhotos);
        adapterAllPhotos.setOnItemClickListener(this);


        bannerBinding.addNewBanner.setOnClickListener(v -> {
            SelectImage();
        });

        loadingAllBanner();
    }

    private void SelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Objects.requireNonNull(this));
    }

    private void loadingAllBanner() {
        bannerBinding.progress.setVisibility(View.VISIBLE);
        refBannerImage
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mAllImageBanner.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelImageBanner imageBanner = snapshot.getValue(ModelImageBanner.class);
                            if (imageBanner != null)
                                mAllImageBanner.add(imageBanner);
                        }
                        adapterAllPhotos.notifyDataSetChanged();

                        if (mAllImageBanner.isEmpty())
                            bannerBinding.emptyRecyclerview.setVisibility(View.VISIBLE);
                        else
                            bannerBinding.emptyRecyclerview.setVisibility(View.GONE);

                        bannerBinding.progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("All Baby Activity", error.getMessage());
                    }
                });
    }

    @Override
    public void onItem_Image_Click(int position) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toasty.normal(Objects.requireNonNull(this), getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri filePath = result.getUri();
                uploadImage(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Error", error.getMessage());
            }
        }
    }


    private void uploadImage(Uri filePath) {
        bannerBinding.progress.setVisibility(View.VISIBLE);
        if (filePath != null) {
            StorageReference ref;
            String ID = String.valueOf(System.currentTimeMillis());
            ref = storageReference
                    .child("/" + ID + ".jpg");
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    ModelImageBanner imageBanner = new ModelImageBanner(ID, uri.toString());
                    refBannerImage.child(ID).setValue(imageBanner);
                    bannerBinding.progress.setVisibility(View.GONE);
                });
            }).addOnFailureListener(e -> {
                bannerBinding.progress.setVisibility(View.GONE);
                Log.d("Failure Error", e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                bannerBinding.progress.setProgress((int) progress);
            });
        }
    }


}