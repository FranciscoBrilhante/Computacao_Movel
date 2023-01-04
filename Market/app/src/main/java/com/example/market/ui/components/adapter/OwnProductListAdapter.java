package com.example.market.ui.components.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.holder.OwnProductViewHolder;

public class OwnProductListAdapter extends ListAdapter<Product, OwnProductViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public OwnProductListAdapter(@NonNull DiffUtil.ItemCallback<Product> diffCallback, RecyclerViewInterface recyclerViewInterface){
        super(diffCallback);
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public OwnProductViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        return OwnProductViewHolder.create(parent,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(OwnProductViewHolder holder, int position) {
        Product current=getItem(position);
        holder.bind(current);
    }

    public static class ProductDiff extends DiffUtil.ItemCallback<Product>{

        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem,@NonNull Product newItem){
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem,@NonNull Product newItem){
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getDescription().equals(newItem.getDescription());
        }
    }
}