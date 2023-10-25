package com.shata.plantdiseasedetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shata.plantdiseasedetectionapp.Adapter.Adapter_All_Plants_Disease;
import com.shata.plantdiseasedetectionapp.Class.ModelPlantsDisease;
import com.shata.plantdiseasedetectionapp.Class.ModelTreatment;
import com.shata.plantdiseasedetectionapp.databinding.ActivityTreatmentBinding;

import java.util.ArrayList;
import java.util.List;

public class TreatmentActivity extends AppCompatActivity implements Adapter_All_Plants_Disease.OnItemClickListener {

    ActivityTreatmentBinding treatmentBinding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refPlantsDisease = database.getReference("PlantsDisease");
    DatabaseReference refTreatments = database.getReference("Treatments");

    Adapter_All_Plants_Disease adapterAllPlantsDisease;
    List<ModelPlantsDisease> plantsDiseaseList;

    String[] classesEn = {
            "Apple Scab", //0
            "Apple Black Rot",//1
            "",//2
            "",//3
            "",//4
            "",//5
            "",//6
            "Corn Maize Cercospora Leaf Spot Gray Leaf Spot",//7
            "Corn Maize Common Rust",//8
            "Corn Maize Northern Leaf Blight",//9
            "",//10
            "Grape Black Rot",//11
            "",//12
            "Grape Leaf Blight",//13
            "",//14
            "Orange Haunglongbing Citrus Greening",//15
            "Peach Bacterial Spot",//16
            "",//17
            "Pepper Bell Bacterial Spot",//18
            "",//19
            "Potato Early Blight",//20
            "Potato Late Blight",//21
            "",//22
            "",//23
            "",//24
            "Squash Powdery Mildew",//25
            "Strawberry Leaf Scorch",//26
            "",//27
            "Tomato Bacterial Spot",//28
            "Tomato Early Blight",//29
            "Tomato Late Blight",//30
            "Tomato Leaf Mold",//31
            "Tomato Septoria Leaf Spot",//32
            "",//33
            "Tomato Target Spot",//34
            "Tomato Yellow Leaf Curl Virus",//35
            "Tomato Mosaic Virus",//36
            ""//37
    };

    String[] classesAr = {
            "جرب التفاح",//0
            "العفن الأسود في التفاح",//1
            "",//2
            "",//3
            "",//4
            "",//5
            "",//6
            "التبقع السركوسبوري أو تبقع الاوراق الرمادي في الذرة الشامية",//7
            "الصدأ العادي علي أوراق الذرة الشامية",//8
            "لفحة الأوراق الجنوبية في الذرة الشامية",//9
            "",//10
            "العفن الأسود في العنب",//11
            "",//12
            "لفحة (تبقع) اوراق العنب",//13
            "",//14
            "مرض أخضرار الموالح (الأخضرار في البرتقال)",//15
            "التبقع البكتيري في الخوخ",//16
            "",//17
            "التبقع البكتيري في الفلفل",//18
            "",//19
            "الندوة المبكرة في البطاطس",//20
            "الندوة المتأخرة في البطاطس",//21
            "",//22
            "",//23
            "",//24
            "البياض الدقيقي في القرع",//25
            "تلطخ أوراق الفراولة",//26
            "",//27
            "التبقع البكتيري في الطماطم",//28
            "الندوة المبكرة في الطماطم",//29
            "الندوة المتأخرة في الطماطم",//30
            "عفن الورقة في الطماطم",//31
            "التبقع السبتوري في الطماطم",//32
            "",//33
            "تبقع تارجت في الطماطم",//34
            "فيروس تجعد واصفرار اوراق الطماطم",//35
            "فيروس موزايك الطماطم",//36
            ""//37
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        treatmentBinding = ActivityTreatmentBinding.inflate(getLayoutInflater());
        setContentView(treatmentBinding.getRoot());


        treatmentBinding.plantsDiseaseRecyclerview.setHasFixedSize(true);
        treatmentBinding.plantsDiseaseRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        plantsDiseaseList = new ArrayList<>();
        adapterAllPlantsDisease = new Adapter_All_Plants_Disease(this, plantsDiseaseList);
        treatmentBinding.plantsDiseaseRecyclerview.setAdapter(adapterAllPlantsDisease);
        adapterAllPlantsDisease.setOnItemClickListener(this);


        //insertData();

        loadingAllPlantsDisease();

    }

    void insertData() {

        String uri = "https://firebasestorage.googleapis.com/v0/b/plant-disease-detection-e81ce.appspot.com/o/1.jpg?alt=media&token=e6057c09-685c-4fd7-a5a5-6999b1cb0568";

        List<Integer> index = new ArrayList<>();
        index.add(0);
        index.add(1);
        index.add(7);
        index.add(8);
        index.add(9);
        index.add(11);
        index.add(13);
        index.add(15);
        index.add(16);
        index.add(18);
        index.add(20);
        index.add(21);
        index.add(25);
        index.add(26);
        index.add(28);
        index.add(29);
        index.add(30);
        index.add(31);
        index.add(32);
        index.add(34);
        index.add(35);
        index.add(36);

        ModelPlantsDisease plantsDisease;
        ModelTreatment treatment;
        String ID = "";

        for (int i = 0; i < index.size(); i++) {

            plantsDisease = new ModelPlantsDisease();
            plantsDisease.setIndex(index.get(i));
            plantsDisease.setImageUri(uri);
            plantsDisease.setNameEn(classesEn[index.get(i)]);
            plantsDisease.setNameAr(classesAr[index.get(i)]);

            ID = String.valueOf(System.currentTimeMillis());

            treatment = new ModelTreatment();
            treatment.setIndex(index.get(i));
            treatment.setID(ID);
            treatment.setTitle("Title");
            treatment.setDescription("Description");

            refPlantsDisease
                    .child(index.get(i).toString())
                    .setValue(plantsDisease);

            refTreatments
                    .child(index.get(i).toString())
                    .child(ID)
                    .setValue(treatment);
        }

    }

    private void loadingAllPlantsDisease() {
        treatmentBinding.progress.setVisibility(View.VISIBLE);
        refPlantsDisease
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        plantsDiseaseList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelPlantsDisease plantsDisease = snapshot.getValue(ModelPlantsDisease.class);
                            if (plantsDisease != null)
                                plantsDiseaseList.add(plantsDisease);
                        }
                        adapterAllPlantsDisease.notifyDataSetChanged();
                        treatmentBinding.progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        treatmentBinding.progress.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onItem_Image_Click(int position) {
        ModelPlantsDisease plantsDisease = plantsDiseaseList.get(position);
        Intent intentPlantsDisease = new Intent(this, PlantsDiseaseActivity.class);
        intentPlantsDisease.putExtra("PlantsDisease", plantsDisease);
        startActivity(intentPlantsDisease);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}