package com.shata.plantdiseasedetectionapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shata.plantdiseasedetectionapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeDetailAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    private List<String> imagesList;
    private ImageView ivDetailImagesProduct;

    public HomeDetailAdapter(Context context, LayoutInflater inflater, List<String> imagesList) {
        this.context    = context;
        this.inflater   = inflater;
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_images_detail_product, container, false);

        setViews(view);
        initViews(position);
        container.addView(view);

        return view;
    }

    //set view
    private void setViews(View view) {
        ivDetailImagesProduct = view.findViewById(R.id.iv_images_detail_product);
    }

    //set init
    private void initViews(int position) {
        String images = imagesList.get(position);

        //ivDetailImagesProduct.setBackgroundResource(images);

        Picasso
                .get()
                .load(images.trim() + "")
                .fit()
                .into(ivDetailImagesProduct);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
