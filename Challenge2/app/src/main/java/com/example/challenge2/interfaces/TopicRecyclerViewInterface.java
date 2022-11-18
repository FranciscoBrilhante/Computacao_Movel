package com.example.challenge2.interfaces;

import com.example.challenge2.notesDatabase.Topic;

public interface TopicRecyclerViewInterface {
    void onLongPress(Topic topic);
    void onClick(Topic topic);
}
