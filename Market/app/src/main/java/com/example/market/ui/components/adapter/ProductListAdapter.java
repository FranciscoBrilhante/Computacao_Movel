package com.example.market.ui.components.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.market.interfaces.RecyclerViewInterface;
import com.example.market.marketDatabase.Message;
import com.example.market.marketDatabase.Product;
import com.example.market.ui.components.holder.ProductViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends ListAdapter<Product, ProductViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private List<Product> products;
    public ProductListAdapter(@NonNull DiffUtil.ItemCallback<Product> diffCallback, RecyclerViewInterface recyclerViewInterface){
        super(diffCallback);
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public void onCurrentListChanged(@NonNull List<Product> previousList, @NonNull List<Product> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        this.products=currentList;
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
        //return super.getItemId(position);
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        return ProductViewHolder.create(parent,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
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