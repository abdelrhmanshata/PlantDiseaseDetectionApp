package com.shata.plantdiseasedetectionapp.Class;

public class ModelImageBanner {

    String ID , ImageUri;

    public ModelImageBanner() {
    }

    public ModelImageBanner(String ID, String imageUri) {
        this.ID = ID;
        ImageUri = imageUri;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
