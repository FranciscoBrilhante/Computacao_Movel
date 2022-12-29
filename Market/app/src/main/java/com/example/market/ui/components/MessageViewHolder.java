package com.example.market.ui.components;

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
import com.example.market.marketDatabase.Message;
import com.google.android.material.imageview.ShapeableImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder{
    private Message message;
    private final View rootView;

    private TextView content;


    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.rootView=itemView;
        this.content=rootView.findViewById(R.id.content);
    }

    public void bind(Message message) {
        this.message=message;

        content.setText(message.getContent());
    }

    static MessageViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_message, parent, false);
        return new MessageViewHolder(view);
    }

}
