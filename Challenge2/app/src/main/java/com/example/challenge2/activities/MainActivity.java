package com.example.challenge2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.challenge2.R;
import com.example.challenge2.fragments.AddFragment;
import com.example.challenge2.fragments.ListFragment;
import com.example.challenge2.fragments.TopicAddFragment;
import com.example.challenge2.fragments.TopicFragment;
import com.example.challenge2.interfaces.AlertInterface;
import com.example.challenge2.interfaces.FragmentNav;
import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.models.NoteViewModelFactory;
import com.example.challenge2.notesDatabase.Note;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FragmentNav, AlertInterface {

    BottomNavigationView bottomNavigationView;
    FragmentManager fragMan;
    NoteViewModel viewModel;
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
        viewModel = new ViewModelProvider(this, new NoteViewModelFactory(getApplication(), this)).get(NoteViewModel.class);
    }

    ListFragment listFragment = new ListFragment();
    TopicFragment topicFragment = new TopicFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notes:
                fragMan.beginTransaction().replace(R.id.main_layout, listFragment,"ListFragment").addToBackStack("notesList").commit();
                return true;

            case R.id.topics:
                fragMan.beginTransaction().replace(R.id.main_layout, topicFragment,"TopicFragment").addToBackStack("topicsList").commit();
                return true;
        }
        return false;
    }


    @Override
    public void NoteListToAddNote() {
        fragMan.beginTransaction().replace(R.id.main_layout, new AddFragment(),"AddFragment").commit();
    }

    @Override
    public void AddNoteToNoteList(Fragment fragment) {
        fragMan.beginTransaction().replace(R.id.main_layout, listFragment,"ListFragment").commit();
        fragMan.beginTransaction().remove(fragment).commit();
    }

    @Override
    public void TopicListToAddTopic() {
        fragMan.beginTransaction().replace(R.id.main_layout, new TopicAddFragment(),"TopicAddFragment").commit();
    }

    @Override
    public void AddTopicToTopicList(Fragment fragment) {
        fragMan.beginTransaction().replace(R.id.main_layout, topicFragment,"TopicFragment").commit();
        fragMan.beginTransaction().remove(fragment).commit();
    }

    @Override
    public void onMessageReceive(Note note) {
        Context context = this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("New message arrived.\nDo you wish to save it?");
        String title= note.getTitle();
        String body= note.getBody();
        if(body.length()>10){
            alert.setMessage("Title: "+title+"\nBody: "+body.substring(0,9)+"...");
        }
        else{
            alert.setMessage("Title: "+title+"\nBody: "+body);
        }
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModel.insert(note);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.show();
    }

    public void showToast(String message){
        Toast toast=Toast.makeText(this,message, Toast.LENGTH_SHORT);
        toast.show();
    }
}