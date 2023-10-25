package com.shata.plantdiseasedetectionapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.shata.plantdiseasedetectionapp.Class.ModelPlantsDisease;
import com.shata.plantdiseasedetectionapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_All_Plants_Disease extends RecyclerView.Adapter<Adapter_All_Plants_Disease.BannerViewHolder> {

    private final Context mContext;
    private final List<ModelPlantsDisease> plantsDiseases;
    private OnItemClickListener mListener;

    public Adapter_All_Plants_Disease(Context mContext, List<ModelPlantsDisease> plantsDiseases) {
        this.mContext = mContext;
        this.plantsDiseases = plantsDiseases;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_plants_disease, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        ModelPlantsDisease plantsDisease = plantsDiseases.get(position);

        try {
            Picasso
                    .get()
                    .load(plantsDisease.getImageUri().trim() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(holder.imageViewPlant);
        } catch (Exception e) {
            Log.d("" + mContext, e.getMessage());
        }

        holder.nameEnTV.setText(plantsDisease.getNameEn().trim());
        holder.nameArTV.setText(plantsDisease.getNameAr().trim());
    }

    @Override
    public int getItemCount() {
        return plantsDiseases.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Image_Click(int position);
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ShapeableImageView imageViewPlant;
        public TextView nameEnTV, nameArTV;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPlant = itemView.findViewById(R.id.imageViewPlant);
            nameEnTV = itemView.findViewById(R.id.nameEnTV);
            nameArTV = itemView.findViewById(R.id.nameArTV);
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
