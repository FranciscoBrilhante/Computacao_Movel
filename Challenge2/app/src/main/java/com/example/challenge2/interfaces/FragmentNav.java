package com.example.challenge2.interfaces;

import androidx.fragment.app.Fragment;

import com.example.challenge2.models.NoteViewModel;

public interface FragmentNav {
    public void NoteListToAddNote();
    public void AddNoteToNoteList(Fragment fragment);
    public void TopicListToAddTopic();
    public void AddTopicToTopicList(Fragment fragment);

}
