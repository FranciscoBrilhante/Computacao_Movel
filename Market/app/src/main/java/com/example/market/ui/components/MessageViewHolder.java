package com.example.market.ui.components;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.Message;
import com.google.android.material.imageview.ShapeableImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    private Message message;
    private final View rootView;
    private TextView content;
    private int profileID;
    private ViewGroup parent;

    public MessageViewHolder(@NonNull View itemView, int profileID, ViewGroup parent) {
        super(itemView);
        this.rootView = itemView;
        this.content = rootView.findViewById(R.id.content);
        this.profileID = profileID;
        this.parent=parent;
    }

    public void bind(Message message) {
        this.message = message;
        if (message.getSender()==profileID) {
            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams) content.getLayoutParams();
            params.gravity = Gravity.END|Gravity.TOP;
            content.setLayoutParams(params);
            content.setBackgroundResource(R.drawable.bubble_outcoming);
        }
        float scale = content.getContext().getResources().getDisplayMetrics().density;
        int size1 = (int) (25*scale + 0.5f);
        int size2 = (int) (8*scale + 0.5f);
        int size4 = (int) (15*scale + 0.5f);
        content.setPadding(size1,size2,size1,size4);
        content.setText(message.getContent());
    }

    static MessageViewHolder create(ViewGroup parent, int profileID) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_message, parent, false);
        return new MessageViewHolder(view, profileID, parent);
    }

}
