package com.example.challenge2;

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

import com.example.challenge2.models.NoteViewModel;
import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.Topic;


public class TopicAddFragment extends Fragment {
    private NoteViewModel noteViewModel;

    private EditText titleView;

    public TopicAddFragment() {
    }

    public static TopicAddFragment newInstance(Topic topic) {
        return new TopicAddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_topic, container, false);
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        titleView=rootView.findViewById(R.id.addTitleView);

        Topic topicSelected=noteViewModel.getTopicSelected();
        if (topicSelected!=null){
            titleView.setText(topicSelected.getTitle());
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

                if (title.isEmpty()){
                    Toast toast=Toast.makeText(getContext(),"Title of topic must not be empty", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    if (noteViewModel.getTopicSelected()==null){
                        noteViewModel.insertTopic(new Topic(titleView.getText().toString()));
                    }
                    else{
                        noteViewModel.updateTopicSelected(titleView.getText().toString());
                    }


                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new TopicFragment(), null).commit();

                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
