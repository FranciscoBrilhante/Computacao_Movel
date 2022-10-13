package com.example.challenge1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link List#newInstance} factory method to
 * create an instance of this fragment.
 */
public class List extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public List() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment List.
     */
    // TODO: Rename and change types and number of parameters
    public static List newInstance(String param1, String param2) {
        List fragment = new List();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_list, container, false);

        MainActivity activity = (MainActivity) getActivity();
        ArrayList<Animal> animals = activity.getAnimals();
        ArrayList<String> animalNames=new ArrayList<>();
        for (Animal animal : animals) {
            animalNames.add(animal.getName());
        }

        Spinner spinner =(Spinner)  rootView.findViewById(R.id.spinner);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(),  android.R.layout.simple_spinner_dropdown_item, animalNames);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> spin,View v, int i,long id){
                MainActivity activity = (MainActivity) getActivity();
                ArrayList<Animal> animals = activity.getAnimals();
                Animal animalSelected = animals.get(i);

                View fragmentView =getActivity().findViewById(R.id.list_layout);
                TextView ownerText= fragmentView.findViewById(R.id.owner);
                TextView nameText= fragmentView.findViewById(R.id.name);
                TextView ageText= fragmentView.findViewById(R.id.age);
                ImageView photo= fragmentView.findViewById(R.id.photo);

                ownerText.setText(animalSelected.getOwner());
                nameText.setText(animalSelected.getName());
                ageText.setText(animalSelected.getAge().toString());

                photo.setImageResource(animalSelected.getImage());

            }
            public void onNothingSelected(AdapterView<?> parent){}
        });

        return rootView;
    }

}