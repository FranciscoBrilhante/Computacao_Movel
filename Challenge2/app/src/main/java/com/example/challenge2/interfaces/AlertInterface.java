package com.example.challenge2.interfaces;

import com.example.challenge2.notesDatabase.Note;

public interface AlertInterface {
    public void onMessageReceive(Note note);
    public void showToast(String message);
}

