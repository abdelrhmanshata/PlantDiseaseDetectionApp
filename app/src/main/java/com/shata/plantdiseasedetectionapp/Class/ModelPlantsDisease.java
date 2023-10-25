package com.shata.plantdiseasedetectionapp.Class;

import java.io.Serializable;

public class ModelPlantsDisease implements Serializable {

    int Index = 0;
    String ImageUri = "";
    String NameEn = "", NameAr = "";

    public ModelPlantsDisease() {
    }

    public ModelPlantsDisease(int index, String imageUri, String nameEn, String nameAr) {
        Index = index;
        ImageUri = imageUri;
        NameEn = nameEn;
        NameAr = nameAr;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

}
