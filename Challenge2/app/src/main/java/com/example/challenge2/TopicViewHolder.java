package com.example.challenge2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.challenge2.notesDatabase.Topic;


public class TopicViewHolder extends RecyclerView.ViewHolder {
    private final TextView topicTitleView;
    private Topic topic;

    private TopicViewHolder(View itemView, TopicRecyclerViewInterface topicRecyclerViewInterface){
        super(itemView);
        topicTitleView=itemView.findViewById(R.id.titleView);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (topicRecyclerViewInterface!=null){
                    int position=getAdapterPosition();

                        if(position!=RecyclerView.NO_POSITION){
                            topicRecyclerViewInterface.onLongPress(topic);
                        }
                }
                return true;
            }
        });
    }

    public void bind(Topic topic){
        this.topic=topic;
        topicTitleView.setText(topic.getTitle());
    }

    static TopicViewHolder create(ViewGroup parent, TopicRecyclerViewInterface topicRecyclerViewInterface){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new TopicViewHolder(view,topicRecyclerViewInterface);
    }
}
