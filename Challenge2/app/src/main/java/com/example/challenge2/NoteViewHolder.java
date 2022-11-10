package com.example.challenge2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.challenge2.notesDatabase.Note;


public class NoteViewHolder extends RecyclerView.ViewHolder {
    private final TextView noteTitleView;
    private final TextView noteBodyView;
    private Note note;

    private NoteViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface){
        super(itemView);
        noteTitleView=itemView.findViewById(R.id.titleView);
        noteBodyView=itemView.findViewById(R.id.bodyView);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (recyclerViewInterface!=null){
                    int position=getAdapterPosition();

                        if(position!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onLongPress(note);
                        }
                }
                return true;
            }
        });
    }

    public void bind(Note note){
        this.note=note;
        noteTitleView.setText(note.getTitle());
        noteBodyView.setText(note.getBody());
    }

    static NoteViewHolder create(ViewGroup parent,RecyclerViewInterface recyclerViewInterface){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new NoteViewHolder(view,recyclerViewInterface);
    }
}
