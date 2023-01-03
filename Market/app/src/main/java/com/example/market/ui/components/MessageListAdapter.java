package com.example.market.ui.components;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.market.interfaces.ContactRecyclerViewInterface;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.Message;

import java.util.List;

public class MessageListAdapter extends ListAdapter<Message,MessageViewHolder> {
    private  final int profileID;
    private List<Message> messages;
    public MessageListAdapter(@NonNull DiffUtil.ItemCallback<Message> diffCallback,int profileID){
        super(diffCallback);
        this.profileID=profileID;
    }

    @Override
    public void onCurrentListChanged(@NonNull List<Message> previousList, @NonNull List<Message> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        this.messages=currentList;
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
        //return super.getItemId(position);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        return MessageViewHolder.create(parent,this.profileID);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message current=getItem(position);
        holder.bind(current);
    }

    public static class MessageDiff extends DiffUtil.ItemCallback<Message>{

        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem,@NonNull Message newItem){
            return oldItem==newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem,@NonNull Message newItem){
            return oldItem.getContent().equals(newItem.getContent()) && oldItem.getTimestamp().equals(newItem.getTimestamp());
        }
    }
}