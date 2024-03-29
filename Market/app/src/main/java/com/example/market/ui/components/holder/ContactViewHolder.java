package com.example.market.ui.components.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.marketDatabase.Contact;
import com.google.android.material.imageview.ShapeableImageView;

public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ContactRecyclerViewInterface recyclerViewInterface;
    private Contact contact;
    private final View rootView;

    private TextView profileName;
    private TextView lastMessage;
    private ShapeableImageView profileIcon;

    public ContactViewHolder(@NonNull View itemView, ContactRecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        this.rootView=itemView;
        this.recyclerViewInterface = recyclerViewInterface;

        this.profileName=rootView.findViewById(R.id.profile_name);
        this.profileIcon=rootView.findViewById(R.id.profile_icon);
        this.lastMessage=rootView.findViewById(R.id.last_message);
    }

    public void bind(Contact contact) {
        this.contact = contact;
        this.profileName.setText(contact.getProfileName());
        this.lastMessage.setText(contact.getLastMessage());
        if (contact.getProfileImage().equals("null")) {
            profileIcon.setImageDrawable(ContextCompat.getDrawable(rootView.getContext(), R.drawable.placeholder_avatar));
        } else {
            String fullURL = "https://" + BuildConfig.API_ADDRESS + contact.getProfileImage();
            try{
                Glide.with(rootView.getContext())
                        .load(fullURL)
                        .override(500, 500) //give resize dimension, you could calculate those
                        .centerCrop() // scale to fill the ImageView
                        .error(R.drawable.placeholder_avatar)
                        .placeholder(R.drawable.placeholder_avatar)
                        .into(profileIcon);
            }catch (Exception ignored){
                profileIcon.setImageDrawable(ContextCompat.getDrawable(rootView.getContext(), R.drawable.placeholder_avatar));
            }
        }

        this.profileIcon.setOnClickListener(this);
        rootView.setOnClickListener(this);

    }

    public static ContactViewHolder create(ViewGroup parent, ContactRecyclerViewInterface recyclerViewInterface) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_contact, parent, false);
        return new ContactViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onClick(View view) {
        recyclerViewInterface.onClick(contact.getProfileID());
    }
}
