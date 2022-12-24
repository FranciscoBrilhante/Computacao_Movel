package com.example.market.ui.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;

import java.util.ArrayList;


public class ProductViewHolder extends RecyclerView.ViewHolder {
    private final TextView titleView;
    private final TextView descriptionView;
    private final ImageSlider imageSlider;

    private Product product;

    private ProductViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        titleView = itemView.findViewById(R.id.title_label);
        descriptionView = itemView.findViewById(R.id.description_label);
        imageSlider= itemView.findViewById(R.id.image_slider);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewInterface.onClick(product);
            }
        });
    }

    public void bind(Product product) {
        this.product = product;
        titleView.setText(product.getTitle());
        descriptionView.setText(product.getDescription());

        ArrayList<SlideModel> imageList =new ArrayList<SlideModel>();
        for(String imgURL: product.getImages()){
            String fullURL="https://"+BuildConfig.API_ADDRESS+imgURL;
            imageList.add(new SlideModel(fullURL, ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(imageList);

    }

    static ProductViewHolder create(ViewGroup parent, RecyclerViewInterface recyclerViewInterface) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_product, parent, false);
        return new ProductViewHolder(view, recyclerViewInterface);
    }
}
