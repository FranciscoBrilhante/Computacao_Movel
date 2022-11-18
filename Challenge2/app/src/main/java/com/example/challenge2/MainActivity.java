package com.example.challenge2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.challenge2.notesDatabase.Topic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FragmentNav {

    BottomNavigationView bottomNavigationView;
    FragmentManager fragMan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Disable auto dark mode

        if (savedInstanceState==null){

            bottomNavigationView = findViewById(R.id.bottomNavigationView);

            fragMan = getSupportFragmentManager();

            bottomNavigationView.setOnNavigationItemSelectedListener(this);
            bottomNavigationView.setSelectedItemId(R.id.notes);
        }

    }

    ListFragment listFragment = new ListFragment();
    TopicFragment topicFragment = new TopicFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notes:
                fragMan.beginTransaction().replace(R.id.main_layout, listFragment).addToBackStack("notesList").commit();
                return true;

            case R.id.topics:
                fragMan.beginTransaction().replace(R.id.main_layout, topicFragment).addToBackStack("topicsList").commit();
                return true;

        }
        return false;
    }


    @Override
    public void NoteListToAddNote() {
        fragMan.beginTransaction().replace(R.id.main_layout, new AddFragment()).commit();
    }

    @Override
    public void AddNoteToNoteList(Fragment fragment) {
        fragMan.beginTransaction().replace(R.id.main_layout, listFragment).commit();
        fragMan.beginTransaction().remove(fragment).commit();
    }

    @Override
    public void TopicListToAddTopic() {
        fragMan.beginTransaction().replace(R.id.main_layout, new TopicAddFragment()).commit();
    }

    @Override
    public void AddTopicToTopicList(Fragment fragment) {
        fragMan.beginTransaction().replace(R.id.main_layout, topicFragment).commit();
        fragMan.beginTransaction().remove(fragment).commit();
    }





}