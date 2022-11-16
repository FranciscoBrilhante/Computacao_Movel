package com.example.challenge2;

import android.util.Log;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.SearchView;

import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.Topic;

import java.util.List;

public class ListFragment extends Fragment implements RecyclerViewInterface {
    private View rootView;
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
        NoteListAdapter adapter = new NoteListAdapter(new NoteListAdapter.NoteDiff(), this);
        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(requireActivity(), notes -> {
            Log.w("ListFragment","Notes List Changed");
            if(!noteViewModel.getSearchText().equals("")) {
                noteViewModel.updateNotesByTitle();
            }
            else{
                adapter.submitList(notes);
            }
        });
        noteViewModel.getNotesByTitle().observe(requireActivity(), notes -> {
            adapter.submitList(notes);
        });


        noteViewModel.subscribeToTopic("cm2022_dyn_dev_123_xpto_456");
        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                noteViewModel.setSearchText(s);
                noteViewModel.updateNotesByTitle();
                return true;
            }

        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                noteViewModel.setSearchText(searchView.getQuery().toString());
                noteViewModel.updateNotesByTitle();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                noteViewModel.setSearchText("");
                noteViewModel.updateNotesByTitle();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_note) {
            noteViewModel.setNoteSelected(null);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new AddFragment(), null).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onLongPress(Note note) {

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuOverlapAnchor);
        PopupMenu popup = new PopupMenu(contextThemeWrapper, rootView);

        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete_option: {
                        noteViewModel.deleteNote(note);
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