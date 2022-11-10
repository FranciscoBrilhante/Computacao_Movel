package com.example.challenge2;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.challenge2.notesDatabase.Note;

public class NoteListAdapter extends ListAdapter<Note,NoteViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public NoteListAdapter(@NonNull DiffUtil.ItemCallback<Note> diffCallback, RecyclerViewInterface recyclerViewInterface){
        super(diffCallback);
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        return NoteViewHolder.create(parent,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note current=getItem(position);
        holder.bind(current);
    }

    static class NoteDiff extends DiffUtil.ItemCallback<Note>{

        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem,@NonNull Note newItem){
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem,@NonNull Note newItem){
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getBody().equals(newItem.getBody());
        }
    }
}
