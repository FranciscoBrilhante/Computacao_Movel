package com.example.challenge2.interfaces;

import com.example.challenge2.notesDatabase.Note;

public interface RecyclerViewInterface {
    void onLongPress(Note note);
    void onClick(Note note);
}