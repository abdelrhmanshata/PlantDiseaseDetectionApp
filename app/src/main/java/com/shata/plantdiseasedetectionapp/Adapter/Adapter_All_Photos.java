package com.shata.plantdiseasedetectionapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shata.plantdiseasedetectionapp.Class.ModelImageBanner;
import com.shata.plantdiseasedetectionapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_All_Photos extends RecyclerView.Adapter<Adapter_All_Photos.BannerViewHolder> {

    private final Context mContext;
    private final List<ModelImageBanner> bannerImages;
    private OnItemClickListener mListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refBannerImage = database.getReference("BannerImages");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("BannerImages");

    public Adapter_All_Photos(Context mContext, List<ModelImageBanner> bannerImages) {
        this.mContext = mContext;
        this.bannerImages = bannerImages;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.raw_image, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        ModelImageBanner imageBanner = bannerImages.get(position);

        try {
            Picasso
                    .get()
                    .load(imageBanner.getImageUri().trim() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(holder.imageViewPlant);
        } catch (Exception e) {
            Log.d("" + mContext, e.getMessage());
        }

        holder.deleteImage.setOnClickListener(v -> {
            storageReference.child(imageBanner.getID()+".jpg").delete();
            refBannerImage.child(imageBanner.getID()).removeValue();
        });


    }

    @Override
    public int getItemCount() {
        return bannerImages.size();
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
        public ImageButton deleteImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPlant = itemView.findViewById(R.id.imageViewPlant);
            deleteImage = itemView.findViewById(R.id.deleteImage);


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
