package com.example.challenge1;

import android.os.Bundle;

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
        // Required empty public constructor
    }

    public static List newInstance() {
        List fragment = new List();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        MainActivity activity = (MainActivity) getActivity();
        ArrayList<Animal> animals = activity.getAnimals();
        ArrayList<String> animalNames = new ArrayList<>();
        for (Animal animal : animals) {
            animalNames.add(animal.getName());
        }

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, animalNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> spin, View v, int i, long id) {
                MainActivity activity = (MainActivity) getActivity();
                ArrayList<Animal> animals = activity.getAnimals();
                Animal animalSelected = animals.get(i);

                View fragmentView = getActivity().findViewById(R.id.list_layout);
                TextView ownerText = fragmentView.findViewById(R.id.owner);
                TextView nameText = fragmentView.findViewById(R.id.name);
                TextView ageText = fragmentView.findViewById(R.id.age);
                ImageView photo = fragmentView.findViewById(R.id.edit_photo);

                ownerText.setText(animalSelected.getOwner());
                nameText.setText(animalSelected.getName());
                ageText.setText(animalSelected.getAge().toString());
                photo.setImageResource(animalSelected.getImage());

                activity.setIndexSelected(i);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Button button = (Button) rootView.findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new Details(), null).commit();
            }
        });
        return rootView;
    }
}