package com.example.challenge2;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.Topic;

import java.util.List;

public class ListFragment extends Fragment implements RecyclerViewInterface {
    private View rootView;
    private NoteViewModel noteViewModel;
    private NoteListAdapter adapter;
    private FragmentNav fragmentNav;
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
        adapter = new NoteListAdapter(new NoteListAdapter.NoteDiff(), this);
        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        fragmentNav = (FragmentNav) getContext();
        adapter.submitList(noteViewModel.getAllNotes().getValue());
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



        //noteViewModel.subscribeToTopic("cm2022_dyn_dev_123_xpto_456");
        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.submitList(noteViewModel.getAllNotes().getValue());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setFocusable(View.FOCUSABLE);
        searchView.setIconified(false);

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
                searchView.requestFocus();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                searchView.setQuery("",true);
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
            noteViewModel.setSearchText("");
            noteViewModel.updateNotesByTitle();
            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new AddFragment(), null).commit();
            fragmentNav.NoteListToAddNote();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onLongPress(Note note) {

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuOverlapAnchor);
        PopupMenu popup = new PopupMenu(contextThemeWrapper, rootView, Gravity.CENTER);

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
                    case R.id.edit_title: {
                        noteViewModel.setNoteSelected(note);
                        popup.dismiss();
                        noteViewModel.setSearchText("");
                        noteViewModel.updateNotesByTitle();
                        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new AddFragment(), null).commit();
                        //fragmentNav.NoteListToAddNote();

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Edit Note Title");
                        final EditText input = new EditText(getContext());
                        input.setText(noteViewModel.getNoteSelected().getTitle());
                        alert.setView(input);
                        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (input.getText().toString().isEmpty()){
                                    Toast toast=Toast.makeText(getContext(),"Title must not be empty", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                else{
                                    noteViewModel.updateNoteSelected(input.getText().toString(), noteViewModel.getNoteSelected().getBody());
                                }

                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                popup.dismiss();
                            }
                        });
                        alert.show();
                        return true;

                    }
                    case R.id.send_to_topics: {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Insert Topics");
                        alert.setMessage("Format: topic1,topic2,topic3,[...]");
                        final EditText input = new EditText(getContext());
                        alert.setView(input);
                        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] topicList = input.getText().toString().split(",");

                                for (String topic:topicList)
                                    noteViewModel.sendToTopic(note,topic);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                popup.dismiss();
                            }
                        });
                        alert.show();
                        return true;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(Note note) {
        noteViewModel.setNoteSelected(note);
        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new AddFragment(), null).commit();
        fragmentNav.NoteListToAddNote();
    }
}