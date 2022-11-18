package com.example.challenge2.interfaces;

import android.view.View;

import com.example.challenge2.notesDatabase.Topic;

public interface TopicRecyclerViewInterface {
    void onLongPress(Topic topic, View view);
    void onClick(Topic topic);
}
