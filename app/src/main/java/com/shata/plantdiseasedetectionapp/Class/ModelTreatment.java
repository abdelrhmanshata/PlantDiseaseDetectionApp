package com.shata.plantdiseasedetectionapp.Class;

import java.io.Serializable;

public class ModelTreatment implements Serializable {

    int Index = 0;
    String ID, Title, Description;

    public ModelTreatment() {
    }

    public ModelTreatment(int index, String ID, String title, String description) {
        Index = index;
        this.ID = ID;
        Title = title;
        Description = description;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
