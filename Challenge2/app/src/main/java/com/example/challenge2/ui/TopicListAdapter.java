package com.example.challenge2.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.challenge2.interfaces.TopicRecyclerViewInterface;
import com.example.challenge2.notesDatabase.Topic;

public class TopicListAdapter extends ListAdapter<Topic,TopicViewHolder> {
    private final TopicRecyclerViewInterface topicRecyclerViewInterface;

    public TopicListAdapter(@NonNull DiffUtil.ItemCallback<Topic> diffCallback, TopicRecyclerViewInterface topicRecyclerViewInterface){
        super(diffCallback);
        this.topicRecyclerViewInterface=topicRecyclerViewInterface;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        return TopicViewHolder.create(parent,topicRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        Topic current=getItem(position);
        holder.bind(current);
    }

    public static class TopicDiff extends DiffUtil.ItemCallback<Topic>{

        @Override
        public boolean areItemsTheSame(@NonNull Topic oldItem,@NonNull Topic newItem){
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Topic oldItem,@NonNull Topic newItem){
            return oldItem.getTitle().equals(newItem.getTitle());
        }
    }
}
