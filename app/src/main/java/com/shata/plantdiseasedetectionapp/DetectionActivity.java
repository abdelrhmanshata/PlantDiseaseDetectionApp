package com.shata.plantdiseasedetectionapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shata.plantdiseasedetectionapp.Class.ModelPlantsDisease;
import com.shata.plantdiseasedetectionapp.databinding.ActivityDetectionBinding;
import com.shata.plantdiseasedetectionapp.ml.PlantDiseaseModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DetectionActivity extends AppCompatActivity {

    private final int imageSize = 224;
    private static final int REQUEST_GALLERY_CODE = 100;
    private static final int REQUEST_CAMERA_CODE = 200;

    ActivityDetectionBinding detectionBinding;
    String[] classesEn = {
            "apple scab", //0
            "apple black rot",//1
            "",//2
            "",//3
            "",//4
            "",//5
            "",//6
            "corn maize cercospora leaf spot gray leaf spot",//7
            "corn maize common rust",//8
            "corn maize northern leaf blight",//9
            "",//10
            "grape black rot",//11
            "",//12
            "grape leaf blight",//13
            "",//14
            "orange haunglongbing citrus greening",//15
            "peach bacterial spot",//16
            "",//17
            "pepper bell bacterial spot",//18
            "",//19
            "potato early blight",//20
            "potato late blight",//21
            "",//22
            "",//23
            "",//24
            "squash powdery mildew",//25
            "strawberry leaf scorch",//26
            "",//27
            "tomato bacterial spot",//28
            "tomato early blight",//29
            "tomato late blight",//30
            "tomato leaf mold",//31
            "tomato septoria leaf spot",//32
            "",//33
            "tomato target spot",//34
            "tomato yellow leaf curl virus",//35
            "tomato mosaic virus",//36
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

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refPlantsDisease = database.getReference("PlantsDisease");

    String finalResultEN = "", finalResultAR = "";
    HashMap<Integer, Integer> allResult = new HashMap<>();
    Animation Bottom_Top, Top_Bottom;

    ModelPlantsDisease plantsDisease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detectionBinding = ActivityDetectionBinding.inflate(getLayoutInflater());
        setContentView(detectionBinding.getRoot());

        plantsDisease = new ModelPlantsDisease();

        detectionBinding.galleryCV.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(DetectionActivity.this);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_CODE);
            }
        });

        detectionBinding.cameraCV.setOnClickListener(v -> {
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                }
            }
        });

        detectionBinding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });


        detectionBinding.moreBtn.setOnClickListener(v -> {

            Intent intentPlantsDisease = new Intent(this, PlantsDiseaseActivity.class);
            intentPlantsDisease.putExtra("PlantsDisease", plantsDisease);
            startActivity(intentPlantsDisease);
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

           /* Top_Bottom = AnimationUtils.loadAnimation(DetectionActivity.this, R.anim.top_to_bottom);
            detectionBinding.resultLayout.setAnimation(Top_Bottom);
            detectionBinding.resultLayout.setVisibility(View.GONE);


            detectionBinding.treatmentLayout.setVisibility(View.VISIBLE);
            Bottom_Top = AnimationUtils.loadAnimation(DetectionActivity.this, R.anim.bottom_to_top);
            detectionBinding.treatmentLayout.setAnimation(Bottom_Top);*/
        });
        detectionBinding.closeBtn.setOnClickListener(v -> {
            Top_Bottom = AnimationUtils.loadAnimation(DetectionActivity.this, R.anim.top_to_bottom);
            detectionBinding.treatmentLayout.setAnimation(Top_Bottom);
            detectionBinding.treatmentLayout.setVisibility(View.GONE);
        });

        detectionBinding.readMore.setOnClickListener(v -> {
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + finalResultEN + "-" + finalResultAR)));
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                detectionBinding.imageViewPlant.setImageURI(resultUri);
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    int dimension = Math.min(image.getWidth(), image.getHeight());
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    classifyImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        } else if (requestCode == REQUEST_CAMERA_CODE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            if (resultCode == RESULT_OK) {
                detectionBinding.imageViewPlant.setImageBitmap(image);
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
    }

    private void classifyImage(Bitmap image) {

        try {
            PlantDiseaseModel model = PlantDiseaseModel.newInstance(getApplicationContext());
            //ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intvalue = new int[imageSize * imageSize];
            image.getPixels(intvalue, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intvalue[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            PlantDiseaseModel.Outputs outputs = model.process(inputFeature0);
            //ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            int Percentage = 0;
            allResult.clear();
            for (int i = 0; i < confidence.length; i++) {
                if (!classesEn[i].isEmpty()) {
                    Percentage = (int) (confidence[i] * 100);
                    if (Percentage > 0) {
                        allResult.put(i, Percentage);
                    }
                    if (confidence[i] > maxConfidence) {
                        maxConfidence = confidence[i];
                        maxPos = i;
                    }
                }
            }

            sortHashMap(allResult, false);
            finalResultEN = classesEn[maxPos];
            finalResultAR = classesAr[maxPos];

            detectionBinding.resultEn.setText(classesEn[maxPos]);
            detectionBinding.resultAr.setText(classesAr[maxPos]);

            loadingPlantsDisease(String.valueOf(maxPos));

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    void sortHashMap(HashMap<Integer, Integer> input, boolean order) {
        //convert HashMap into List
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(input.entrySet());
        //sorting the list elements
        Collections.sort(list, (o1, o2) -> {
            if (order) {
                //compare two object and return an integer
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //prints the sorted HashMap
        Map<Integer, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        setPieChartValue(sortedMap);
    }

    void setPieChartValue(Map<Integer, Integer> map) {
        Bottom_Top = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        detectionBinding.resultLayout.setVisibility(View.VISIBLE);
        detectionBinding.resultLayout.setAnimation(Bottom_Top);
        ArrayList<PieEntry> detectionResult = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            detectionResult.add(new PieEntry(entry.getValue(), classesEn[entry.getKey()]));
        }

        PieDataSet pieDataSet = new PieDataSet(detectionResult, "Detection Result");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        detectionBinding.pieChart.setData(pieData);
        detectionBinding.pieChart.getDescription().setEnabled(false);
        detectionBinding.pieChart.setCenterText("Detection Result");
        detectionBinding.pieChart.animate();
    }

    void loadingPlantsDisease(String index) {
        refPlantsDisease
                .child(index)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelPlantsDisease objDisease = dataSnapshot.getValue(ModelPlantsDisease.class);
                        if (objDisease != null) {
                            plantsDisease = objDisease;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}