package com.example.challenge2.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge2.interfaces.AlertInterface;
import com.example.challenge2.interfaces.FragmentNav;
import com.example.challenge2.R;
import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.models.NoteViewModelFactory;
import com.example.challenge2.notesDatabase.Note;


public class AddFragment extends Fragment {
    private NoteViewModel noteViewModel;
    private FragmentNav fragmentNav;

    private EditText titleView;
    private EditText bodyView;

    public AddFragment() {
    }

    public static AddFragment newInstance(Note note) {
        return new AddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        fragmentNav = (FragmentNav) getContext();
        titleView=rootView.findViewById(R.id.addTitleView);
        bodyView= rootView.findViewById(R.id.addBodyView);

        Note noteSelected=noteViewModel.getNoteSelected();
        if (noteSelected!=null){
            titleView.setText(noteSelected.getTitle());
            bodyView.setText(noteSelected.getBody());
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note: {
                String title = titleView.getText().toString();
                String body= bodyView.getText().toString();

                if (title.isEmpty() || body.isEmpty()){
                    Toast toast=Toast.makeText(getContext(),"Title and body of note must not be empty", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    if (noteViewModel.getNoteSelected()==null){
                        noteViewModel.insert(new Note(titleView.getText().toString(),bodyView.getText().toString()));
                    }
                    else{
                        noteViewModel.updateNoteSelected(titleView.getText().toString(),bodyView.getText().toString());
                    }
                    fragmentNav.AddNoteToNoteList(this);
                }
                return true;
            }
            case R.id.discard:{
                fragmentNav.AddNoteToNoteList(this);
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
