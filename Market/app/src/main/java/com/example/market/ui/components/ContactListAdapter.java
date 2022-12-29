package com.example.market.ui.components;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.Product;

public class ContactListAdapter extends ListAdapter<Contact,ContactViewHolder> {
    private final ContactRecyclerViewInterface recyclerViewInterface;

    public ContactListAdapter(@NonNull DiffUtil.ItemCallback<Contact> diffCallback, ContactRecyclerViewInterface recyclerViewInterface){
        super(diffCallback);
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        return ContactViewHolder.create(parent,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact current=getItem(position);
        holder.bind(current);
    }

    public static class ContactDiff extends DiffUtil.ItemCallback<Contact>{

        @Override
        public boolean areItemsTheSame(@NonNull Contact oldItem,@NonNull Contact newItem){
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Contact oldItem,@NonNull Contact newItem){
            return oldItem.getProfileName().equals(newItem.getProfileName());
        }
    }
}