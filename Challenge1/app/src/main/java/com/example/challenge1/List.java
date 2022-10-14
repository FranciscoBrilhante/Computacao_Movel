package com.example.challenge1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class List extends Fragment {
    public List() {
    }

    public static List newInstance() {
        return new List();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity activity = (MainActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        ArrayList<Animal> animals = activity.getAnimals();
        Spinner spinner = rootView.findViewById(R.id.spinner);
        CustomAdapter adapter = new CustomAdapter(getContext(), animals);
        spinner.setAdapter(adapter);
        spinner.setSelection(activity.getIndexSelected());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> spin, View v, int i, long id) {
                MainActivity activity = (MainActivity) getActivity();
                View fragmentView = activity.findViewById(R.id.list_layout);

                activity.setIndexSelected(i);
                fillValues(fragmentView, activity);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button button = rootView.findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new Details(), null).commit();
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("E suposto guardar");
    }

    private void fillValues(View view, MainActivity mainActivity) {
        Animal animalSelected = mainActivity.getAnimal(mainActivity.getIndexSelected());

        TextView ownerText = view.findViewById(R.id.owner);
        TextView nameText = view.findViewById(R.id.name);
        TextView ageText = view.findViewById(R.id.age);
        ImageView photo = view.findViewById(R.id.edit_photo);

        ownerText.setText(animalSelected.getOwner());
        nameText.setText(animalSelected.getName());
        ageText.setText(animalSelected.getAge().toString());
        photo.setImageResource(animalSelected.getImage());
    }
}