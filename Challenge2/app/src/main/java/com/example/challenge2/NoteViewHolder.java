package com.example.challenge2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class NoteViewHolder extends RecyclerView.ViewHolder {
    private final TextView noteTitleView;
    private final TextView noteBodyView;

    private NoteViewHolder(View itemView){
        super(itemView);
        noteTitleView=itemView.findViewById(R.id.titleView);
        noteBodyView=itemView.findViewById(R.id.bodyView);
    }

    public void bind(String title,String body){
        noteTitleView.setText(title);
        noteBodyView.setText(body);
    }

    static NoteViewHolder create(ViewGroup parent){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new NoteViewHolder(view);
    }
}
