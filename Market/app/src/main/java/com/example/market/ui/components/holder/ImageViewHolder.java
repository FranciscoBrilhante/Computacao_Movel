package com.example.market.ui.components.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.R;
import com.example.market.interfaces.ProductImageInterface;
import com.example.market.marketDatabase.Image;
import com.google.android.material.imageview.ShapeableImageView;

public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ShapeableImageView imageView;
    private final AppCompatImageButton removeButton;
    private final ProductImageInterface productImageInterface;
    private Image image;
    private ImageViewHolder(View itemView, ProductImageInterface productImageInterface){
        super(itemView);
        imageView=itemView.findViewById(R.id.image_view);
        removeButton=itemView.findViewById(R.id.remove_photo);
        this.productImageInterface=productImageInterface;

    }

    public void bind(Image image){
        this.image=image;
        System.out.println(image.getPath());
        if(image.getPath()==null){
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), R.drawable.placeholder_no_image_pt));
        }
        else{
            imageView.setImageBitmap(image.getContent());
        }
        removeButton.setOnClickListener(this);
    }

    static ImageViewHolder create(ViewGroup parent,ProductImageInterface productImageInterface){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_image,parent,false);
        return new ImageViewHolder(view,productImageInterface);
    }

    @Override
    public void onClick(View view) {
        productImageInterface.onClickToRemove(image);
    }
}