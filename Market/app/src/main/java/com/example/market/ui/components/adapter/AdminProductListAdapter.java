package com.example.market.ui.components.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.holder.AdminProductViewHolder;

public class AdminProductListAdapter extends ListAdapter<Product, AdminProductViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public AdminProductListAdapter(@NonNull DiffUtil.ItemCallback<Product> diffCallback, RecyclerViewInterface recyclerViewInterface){
        super(diffCallback);
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public AdminProductViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        return AdminProductViewHolder.create(parent,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(AdminProductViewHolder holder, int position) {
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