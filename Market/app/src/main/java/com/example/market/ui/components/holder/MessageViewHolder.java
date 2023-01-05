package com.example.market.ui.components.holder;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.R;
import com.example.market.marketDatabase.Message;

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
        float scale = content.getContext().getResources().getDisplayMetrics().density;
        int size2 = (int) (8*scale + 0.5f);
        int size4 = (int) (12*scale + 0.5f);

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams) content.getLayoutParams();

        if (message.getSender()==profileID) {
            params.gravity = Gravity.END;
            content.setLayoutParams(params);
            content.setBackgroundResource(R.drawable.bubble_outcoming);
            int size1 = (int) (25*scale + 0.5f);
            int size3 = (int) (17*scale + 0.5f);
            content.setPadding(size3,size2,size1,size4);
        }
        else{
            params.gravity = Gravity.START;
            content.setLayoutParams(params);
            content.setBackgroundResource(R.drawable.bubble_incoming);
            int size1 = (int) (17*scale + 0.5f);
            int size3 = (int) (25*scale + 0.5f);
            content.setPadding(size3,size2,size1,size4);
        }
        content.setText(message.getContent());
    }

    public static MessageViewHolder create(ViewGroup parent, int profileID) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_message, parent, false);
        return new MessageViewHolder(view, profileID, parent);
    }

}
