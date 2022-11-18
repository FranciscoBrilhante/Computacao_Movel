package com.example.challenge2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.Topic;

import java.util.List;

public class TopicFragment extends Fragment implements TopicRecyclerViewInterface {
    private View rootView;
    private NoteViewModel noteViewModel;
    private FragmentNav fragmentNav;


    public TopicFragment() {
    }

    public static TopicFragment newInstance() {
        return new TopicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);
        RecyclerView topicRecyclerView = rootView.findViewById(R.id.recycler_view);
        TopicListAdapter adapter = new TopicListAdapter(new TopicListAdapter.TopicDiff(), this);
        topicRecyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        topicRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        fragmentNav = (FragmentNav) getContext();

        noteViewModel.getAllTopics().observe(requireActivity(), topics -> {
            Log.w("TopicFragment","Topics List Changed");
            if(!noteViewModel.getSearchText().equals("")) {
                noteViewModel.updateTopicsByTitle();
            }
            else{
                adapter.submitList(topics);
            }

            //noteViewModel.subscribeToTopic(topics.get(topics.size()-1).getTitle());
        });

        noteViewModel.getTopicsByTitle().observe(requireActivity(), topics -> {
            adapter.submitList(topics);
        });

        // noteViewModel.subscribeToTopic("cm2022_dyn_dev_123_xpto_456");
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
                noteViewModel.updateTopicsByTitle();
                return true;
            }

        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                noteViewModel.setSearchText(searchView.getQuery().toString());
                noteViewModel.updateTopicsByTitle();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                noteViewModel.setSearchText("");
                noteViewModel.updateTopicsByTitle();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_note) {
            noteViewModel.setTopicSelected(null);
            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new TopicAddFragment(), null).commit();
            fragmentNav.TopicListToAddTopic();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onLongPress(Topic topic) {

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuOverlapAnchor);
        PopupMenu popup = new PopupMenu(contextThemeWrapper, rootView);

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
                        noteViewModel.setSearchText("");
                        noteViewModel.updateTopicsByTitle();

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Edit Topic");
                        final EditText input = new EditText(getContext());
                        input.setText(noteViewModel.getTopicSelected().getTitle());
                        alert.setView(input);
                        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (input.getText().toString().isEmpty()){
                                    Toast toast=Toast.makeText(getContext(),"Title must not be empty", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                else{
                                    noteViewModel.updateTopicSelected(input.getText().toString());
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
                }
                return true;
            }
        });
    }


    @Override
    public void onClick(Topic topic) {
        noteViewModel.setTopicSelected(topic);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new TopicAddFragment(), null).commit();
    }
}