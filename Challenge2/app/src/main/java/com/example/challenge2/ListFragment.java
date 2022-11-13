package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.notesDatabase.Note;

public class ListFragment extends Fragment implements RecyclerViewInterface {
    private View rootview;
    private NoteViewModel noteViewModel;

    public ListFragment() {
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        final NoteListAdapter adapter = new NoteListAdapter(new NoteListAdapter.NoteDiff(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        noteViewModel.getAllNotes().observe(requireActivity(), notes -> {
            adapter.submitList(notes);
        });
        this.rootview = rootView;
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_note: {
                noteViewModel.setNoteSelected(null);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new AddFragment(), null).commit();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onLongPress(Note note) {

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuOverlapAnchor);
        PopupMenu popup = new PopupMenu(contextThemeWrapper, rootview);
        //PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(),rootview, Gravity.NO_GRAVITY, androidx.appcompat.R.attr.actionOverflowMenuStyle,0);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete_option: {
                        noteViewModel.deleteNote(note);
                        System.out.println("Pressed delete");
                        popup.dismiss();
                        return true;
                    }
                    case R.id.edit_option: {
                        noteViewModel.setNoteSelected(note);
                        popup.dismiss();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new AddFragment(), null).commit();
                        return true;
                    }
                }
                return true;
            }
        });
    }
}