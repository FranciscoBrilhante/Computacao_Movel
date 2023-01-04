package com.example.market.ui.components.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.market.interfaces.ProductImageInterface;
import com.example.market.marketDatabase.Image;
import com.example.market.ui.components.holder.ImageViewHolder;

public class ImageListAdapter extends ListAdapter<Image, ImageViewHolder> {
    private ProductImageInterface productImageInterface;
    public ImageListAdapter(@NonNull DiffUtil.ItemCallback<Image> diffCallback, ProductImageInterface productImageInterface){
        super(diffCallback);
        this.productImageInterface=productImageInterface;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ImageViewHolder.create(parent,productImageInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image current=getItem(position);
        holder.bind(current);
    }

    public static class ImageDiff extends DiffUtil.ItemCallback<Image>{

        @Override
        public boolean areItemsTheSame(@NonNull Image oldItem,@NonNull Image newItem){
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Image oldItem,@NonNull Image newItem){
            return oldItem.getPath().equals(newItem.getPath()) && oldItem.getContent().sameAs(newItem.getContent());
        }
    }
}
