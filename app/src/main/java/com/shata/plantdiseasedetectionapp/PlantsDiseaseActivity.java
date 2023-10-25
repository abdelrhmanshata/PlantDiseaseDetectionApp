package com.shata.plantdiseasedetectionapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shata.plantdiseasedetectionapp.Adapter.Adapter_All_Treatment;
import com.shata.plantdiseasedetectionapp.Class.Admin;
import com.shata.plantdiseasedetectionapp.Class.ModelPlantsDisease;
import com.shata.plantdiseasedetectionapp.Class.ModelTreatment;
import com.shata.plantdiseasedetectionapp.databinding.ActivityPlantsDiseaseBinding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class PlantsDiseaseActivity extends AppCompatActivity implements Adapter_All_Treatment.OnItemClickListener {

    ActivityPlantsDiseaseBinding plantsDiseaseBinding;

    ModelPlantsDisease plantsDisease;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refPlantsDisease = database.getReference("PlantsDisease");
    DatabaseReference refTreatments = database.getReference("Treatments");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("PlantsDiseaseImages");

    Adapter_All_Treatment adapterAllTreatment;
    List<ModelTreatment> treatments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plantsDiseaseBinding = ActivityPlantsDiseaseBinding.inflate(getLayoutInflater());
        setContentView(plantsDiseaseBinding.getRoot());

        plantsDisease = (ModelPlantsDisease) getIntent().getSerializableExtra("PlantsDisease");

        // Permissions to open Camera
        ActivityCompat.requestPermissions(PlantsDiseaseActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        plantsDiseaseBinding.treatmentRecyclerview.setHasFixedSize(true);
        plantsDiseaseBinding.treatmentRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        treatments = new ArrayList<>();
        adapterAllTreatment = new Adapter_All_Treatment(this, treatments);
        adapterAllTreatment.setModelPlantsDisease(plantsDisease);
        plantsDiseaseBinding.treatmentRecyclerview.setAdapter(adapterAllTreatment);
        adapterAllTreatment.setOnItemClickListener(this);

        if (Admin.isAdmin) {
            plantsDiseaseBinding.addNewTreatment.setVisibility(View.VISIBLE);
        } else {
            plantsDiseaseBinding.addNewTreatment.setVisibility(View.GONE);
        }

        plantsDiseaseBinding
                .imageViewPlant
                .setOnClickListener(v -> {
                    if (Admin.isAdmin) {
                        SelectImage();
                    } else {
                        Intent intentZooming = new Intent(this, ZoomingImageActivity.class);
                        intentZooming.putExtra("ImageUri", plantsDisease.getImageUri());
                        startActivity(intentZooming);
                        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

        plantsDiseaseBinding.addNewTreatment.setOnClickListener(v -> {
            showDialogTreatment(null);
        });

        plantsDiseaseBinding.moreBtn.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + plantsDisease.getNameEn() + "-" + plantsDisease.getNameAr())));
        });

        loadingTreatments();
        loadingPlantsDisease();
    }

    private void loadingTreatments() {
        plantsDiseaseBinding.progress.setVisibility(View.VISIBLE);
        refTreatments
                .child(String.valueOf(plantsDisease.getIndex()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        treatments.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelTreatment treatment = snapshot.getValue(ModelTreatment.class);
                            if (treatment != null)
                                treatments.add(treatment);
                        }
                        plantsDiseaseBinding.progress.setVisibility(View.GONE);
                        adapterAllTreatment.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    void loadingPlantsDisease() {
        refPlantsDisease
                .child(String.valueOf(plantsDisease.getIndex()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelPlantsDisease plantsDisease = dataSnapshot.getValue(ModelPlantsDisease.class);
                        if (plantsDisease != null) {
                            try {
                                Picasso
                                        .get()
                                        .load(plantsDisease.getImageUri().trim() + "")
                                        .fit()
                                        .placeholder(R.drawable.loading)
                                        .into(plantsDiseaseBinding.imageViewPlant);
                            } catch (Exception ignored) {
                            }

                            plantsDiseaseBinding.nameEn.setText(plantsDisease.getNameEn().trim());
                            plantsDiseaseBinding.nameAr.setText(plantsDisease.getNameAr().trim());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
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
        plantsDiseaseBinding.waveLoadingView.setVisibility(View.VISIBLE);
        if (filePath != null) {
            StorageReference ref;
            String ID = String.valueOf(System.currentTimeMillis());
            ref = storageReference
                    .child("/" + plantsDisease.getIndex() + ".jpg");
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    refPlantsDisease.child(String.valueOf(plantsDisease.getIndex())).child("imageUri").setValue(uri.toString());
                    plantsDiseaseBinding.waveLoadingView.setVisibility(View.GONE);
                });
            }).addOnFailureListener(e -> {
                plantsDiseaseBinding.waveLoadingView.setVisibility(View.GONE);
                Log.d("Failure Error", e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                plantsDiseaseBinding.waveLoadingView.setCenterTitle((int) progress + " %");
                plantsDiseaseBinding.waveLoadingView.setProgressValue((int) progress);
            });
        }
    }


    @Override
    public void onItem_Image_Click(int position) {
        if (Admin.isAdmin) {
            ModelTreatment treatment = treatments.get(position);
            showDialogTreatment(treatment);
        }
    }

    @SuppressLint("CheckResult")
    public void showDialogTreatment(ModelTreatment treatment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_treatment_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        //
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);
        //
        TextInputEditText Title = dialogView.findViewById(R.id.Title);
        TextInputEditText Description = dialogView.findViewById(R.id.Description);
        RelativeLayout layout = dialogView.findViewById(R.id.addQuestion);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressSave);

        if (treatment != null) {
            Title.setText(treatment.getTitle());
            Description.setText(treatment.getDescription());
        }

        layout.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String sTitle = Objects.requireNonNull(Title.getText()).toString().trim();
            if (sTitle.isEmpty()) {
                Title.setError(getString(R.string.PleaseEnterTitle));
                Title.setFocusable(true);
                Title.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }
            String sDescription = Objects.requireNonNull(Description.getText()).toString().trim();
            if (sDescription.isEmpty()) {
                Description.setError(getString(R.string.PleaseEnterDescription));
                Description.setFocusable(true);
                Description.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }

            ModelTreatment modelTreatment = new ModelTreatment();
            if (treatment == null) {
                modelTreatment.setID(String.valueOf(System.currentTimeMillis()));
            } else {
                modelTreatment.setID(treatment.getID());
            }
            modelTreatment.setIndex(plantsDisease.getIndex());
            modelTreatment.setTitle(sTitle);
            modelTreatment.setDescription(sDescription);

            refTreatments
                    .child(String.valueOf(plantsDisease.getIndex()))
                    .child(modelTreatment.getID())
                    .setValue(modelTreatment);

            progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
        });
    }


}