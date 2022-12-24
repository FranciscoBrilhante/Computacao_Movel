package com.example.market.ui.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ProductViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener, ItemClickListener {
    private final TextView titleView;
    private final TextView descriptionView;
    private final TextView ratingTextView;
    private final TextView categoryTextView;
    private final TextView locationTextView;
    private final TextView priceTextView;
    private final TextView dateTextView;
    private final ImageSlider imageSlider;
    private final TextView nameTextView;

    private final RecyclerViewInterface recyclerViewInterface;
    private Product product;

    private ProductViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        this.recyclerViewInterface=recyclerViewInterface;

        titleView = itemView.findViewById(R.id.title_label);
        descriptionView = itemView.findViewById(R.id.description_label);
        imageSlider= itemView.findViewById(R.id.image_slider);
        ratingTextView=itemView.findViewById(R.id.rating_label);
        categoryTextView=itemView.findViewById(R.id.category_label);
        locationTextView=itemView.findViewById(R.id.city_label);
        priceTextView=itemView.findViewById(R.id.price_label);
        dateTextView=itemView.findViewById(R.id.date_label);
        nameTextView=itemView.findViewById(R.id.profile_label);

        itemView.setOnClickListener(this);
    }

    public void bind(Product product) {
        this.product = product;
        titleView.setText(product.getTitle());
        descriptionView.setText(product.getDescription());
        ratingTextView.setText(String.format(Locale.ENGLISH,"%.1f", product.getProfileRating() ));
        categoryTextView.setText(product.getCategoryName());
        locationTextView.setText("Coimbra");
        priceTextView.setText(String.format(Locale.ENGLISH,"%.0f€", product.getPrice()));
        nameTextView.setText(product.getProfileName());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy",Locale.ENGLISH);
        String strDate = dateFormat.format(product.getDate().getTime());
        dateTextView.setText(strDate);

        ArrayList<SlideModel> imageList =new ArrayList<SlideModel>();
        for(String imgURL: product.getImages()){
            String fullURL="https://"+BuildConfig.API_ADDRESS+imgURL;
            imageList.add(new SlideModel(fullURL, ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(imageList);
        imageSlider.setItemClickListener(this);
    }

    static ProductViewHolder create(ViewGroup parent, RecyclerViewInterface recyclerViewInterface) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_product, parent, false);
        return new ProductViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onClick(View view) {
        recyclerViewInterface.onClick(product);
    }

    @Override
    public void onItemSelected(int i) {
        recyclerViewInterface.onClick(product);
    }
}
