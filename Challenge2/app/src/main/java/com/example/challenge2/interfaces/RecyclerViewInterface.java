package com.example.challenge2.interfaces;

import android.view.View;

import com.example.challenge2.notesDatabase.Note;

public interface RecyclerViewInterface {
    void onLongPress(Note note, View view);
    void onClick(Note note);
}
