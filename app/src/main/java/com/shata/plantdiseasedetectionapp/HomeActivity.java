package com.shata.plantdiseasedetectionapp;

import static maes.tech.intentanim.CustomIntent.customType;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shata.plantdiseasedetectionapp.Adapter.HomeDetailAdapter;
import com.shata.plantdiseasedetectionapp.Class.Admin;
import com.shata.plantdiseasedetectionapp.Class.ModelImageBanner;
import com.shata.plantdiseasedetectionapp.databinding.ActivityHomeBinding;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding homeBinding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refBannerImage = database.getReference("BannerImages");

    //private ImageButton buttonBack;
    private final List<String> imagesList = new ArrayList<>();
    private ViewPager viewPager;
    private HomeDetailAdapter adapter;
    private WormDotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        setViews();
        initViews();
        initData();

    }

    private void setViews() {
        viewPager = findViewById(R.id.view_pager);
        if (Admin.isAdmin) {
            homeBinding.banner.setVisibility(View.VISIBLE);
            homeBinding.treatment.setVisibility(View.VISIBLE);
        } else {
            homeBinding.banner.setVisibility(View.GONE);
            homeBinding.treatment.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        homeBinding.scanPlant.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, DetectionActivity.class));
            customType (HomeActivity.this, "left-to-right");
        });
        homeBinding.treatment.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, TreatmentActivity.class));
            customType(HomeActivity.this, "left-to-right");
        });
        homeBinding.banner.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, BannerActivity.class));
            customType(HomeActivity.this, "left-to-right");
        });
    }

    private void initData() {

        adapter = new HomeDetailAdapter(getApplicationContext(), getLayoutInflater(), imagesList);
        viewPager.setAdapter(adapter);

        dotsIndicator = findViewById(R.id.layout_dot);
        dotsIndicator.setViewPager(viewPager);
        homeBinding.progressBanner.setVisibility(View.VISIBLE);

        refBannerImage
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        imagesList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelImageBanner imageBanner = snapshot.getValue(ModelImageBanner.class);
                            if (imageBanner != null)
                                imagesList.add(imageBanner.getImageUri());
                        }
                        adapter.notifyDataSetChanged();
                        homeBinding.progressBanner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        homeBinding.progressBanner.setVisibility(View.GONE);
                    }
                });
    }

}