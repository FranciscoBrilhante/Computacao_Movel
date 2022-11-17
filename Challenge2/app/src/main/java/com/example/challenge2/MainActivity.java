package com.example.challenge2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.challenge2.notesDatabase.Topic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Disable auto dark mode

        if (savedInstanceState==null){

            bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
                getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, listFragment).commit();
                return true;

            case R.id.topics:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, topicFragment).commit();
                return true;

        }
        return false;
    }

}