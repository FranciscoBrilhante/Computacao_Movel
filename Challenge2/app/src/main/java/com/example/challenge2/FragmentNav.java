package com.example.challenge2;

import androidx.fragment.app.Fragment;

public interface FragmentNav {
    public void NoteListToAddNote();
    public void AddNoteToNoteList(Fragment fragment);
    public void TopicListToAddTopic();
    public void AddTopicToTopicList(Fragment fragment);
}
