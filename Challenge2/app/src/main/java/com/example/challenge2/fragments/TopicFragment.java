package com.example.challenge2.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challenge2.interfaces.FragmentNav;
import com.example.challenge2.R;
import com.example.challenge2.ui.TopicListAdapter;
import com.example.challenge2.interfaces.TopicRecyclerViewInterface;
import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.notesDatabase.Topic;
import android.provider.Settings.Secure;

public class TopicFragment extends Fragment implements TopicRecyclerViewInterface {
    private NoteViewModel noteViewModel;
    private FragmentNav fragmentNav;
    private TopicListAdapter adapter;

    public TopicFragment() {
    }

    public static TopicFragment newInstance() {
        return new TopicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new TopicListAdapter(new TopicListAdapter.TopicDiff(), this);

        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        fragmentNav = (FragmentNav) getContext();

        noteViewModel.getAllTopics().observe(requireActivity(), topics -> {
            Log.w("TopicFragment", "Topics list changed");
            if (!noteViewModel.getSearchTextTopic().equals("")) {
                noteViewModel.updateTopicsByTitle();
            } else {
                adapter.submitList(topics);
            }
        });
        noteViewModel.getTopicsByTitle().observe(requireActivity(), topics -> {
            adapter.submitList(topics);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);
        RecyclerView topicRecyclerView = rootView.findViewById(R.id.recycler_view);

        topicRecyclerView.setAdapter(adapter);
        topicRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.submitList(noteViewModel.getAllTopics().getValue());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_topic, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search_topic);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setFocusable(View.FOCUSABLE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                noteViewModel.setSearchTextTopic(s);
                noteViewModel.updateTopicsByTitle();
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
                searchView.setQuery("", true);
                noteViewModel.setSearchTextTopic("");
                noteViewModel.updateTopicsByTitle();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_note_topic) {
            noteViewModel.setTopicSelected(null);
            fragmentNav.TopicListToAddTopic();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLongPress(Topic topic, View view) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuOverlapAnchor);
        PopupMenu popup = new PopupMenu(contextThemeWrapper, view);
        popup.getMenuInflater().inflate(R.menu.menu_popup_topic, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete_option: {
                        noteViewModel.deleteTopic(topic);
                        noteViewModel.unsubscribeFromTopic(topic.getTitle());
                        popup.dismiss();
                        return true;
                    }
                    case R.id.edit_title: {
                        noteViewModel.setTopicSelected(topic);
                        popup.dismiss();
                        noteViewModel.setSearchTextTopic("");
                        noteViewModel.updateTopicsByTitle();

                        Context context = getContext();
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setTitle("Edit Topic");
                        final EditText input = new EditText(context);
                        input.setText(noteViewModel.getTopicSelected().getTitle());
                        alert.setView(input);
                        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (input.getText().toString().isEmpty()) {
                                    Toast toast = Toast.makeText(getContext(), "Title must not be empty", Toast.LENGTH_LONG);
                                    toast.show();
                                } else {
                                    noteViewModel.updateTopicSelected(input.getText().toString());
                                }

                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
    public void onClick(Topic topic) {
        noteViewModel.setTopicSelected(topic);
        fragmentNav.TopicListToAddTopic();
    }


}