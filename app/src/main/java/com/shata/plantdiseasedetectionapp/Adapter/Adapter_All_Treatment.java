package com.shata.plantdiseasedetectionapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shata.plantdiseasedetectionapp.Class.Admin;
import com.shata.plantdiseasedetectionapp.Class.ModelPlantsDisease;
import com.shata.plantdiseasedetectionapp.Class.ModelTreatment;
import com.shata.plantdiseasedetectionapp.R;

import java.util.List;

public class Adapter_All_Treatment extends RecyclerView.Adapter<Adapter_All_Treatment.TreatmentViewHolder> {

    private final Context mContext;
    private final List<ModelTreatment> treatments;
    private ModelPlantsDisease plantsDisease;
    private OnItemClickListener mListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refTreatments = database.getReference("Treatments");

    public Adapter_All_Treatment(Context mContext, List<ModelTreatment> treatments) {
        this.mContext = mContext;
        this.treatments = treatments;
    }

    public void setModelPlantsDisease(ModelPlantsDisease plantsDisease) {
        this.plantsDisease = plantsDisease;
    }

    @NonNull
    @Override
    public TreatmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_treatment, parent, false);
        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentViewHolder holder, int position) {
        ModelTreatment treatment = treatments.get(position);


        holder.Title.setText(treatment.getTitle().trim());
        holder.Description.setText(treatment.getDescription().trim());

        if (Admin.isAdmin) {
            holder.deleteTreatment.setVisibility(View.VISIBLE);
            holder
                    .deleteTreatment
                    .setOnClickListener(v -> {
                        {
                            refTreatments
                                    .child(String.valueOf(plantsDisease.getIndex()))
                                    .child(treatment.getID())
                                    .removeValue();
                            ;
                        }
                    });
        } else {
            holder.deleteTreatment.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Image_Click(int position);
    }

    public class TreatmentViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ImageButton deleteTreatment;
        public TextView Title, Description;

        public TreatmentViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteTreatment = itemView.findViewById(R.id.deleteTreatment);
            Title = itemView.findViewById(R.id.Title);
            Description = itemView.findViewById(R.id.Description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Image_Click(position);
                }
            }
        }
    }
}
