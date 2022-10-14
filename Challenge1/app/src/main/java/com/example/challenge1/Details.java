package com.example.challenge1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Details extends Fragment {
    public Details() {
    }

    public static Details newInstance() {
        return new Details();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        MainActivity activity = (MainActivity) getActivity();
        Animal animalSelected = activity.getAnimal(activity.getIndexSelected());

        TextView ownerText = rootView.findViewById(R.id.edit_owner);
        TextView nameText = rootView.findViewById(R.id.edit_name);
        TextView ageText = rootView.findViewById(R.id.edit_age);
        ImageView photo = rootView.findViewById(R.id.edit_photo);

        ownerText.setText(animalSelected.getOwner());
        nameText.setText(animalSelected.getName());
        ageText.setText(animalSelected.getAge().toString());
        photo.setImageResource(animalSelected.getImage());

        Button button = rootView.findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                Integer index = activity.getIndexSelected();
                activity.setAnimal(index, ownerText.getText().toString(), nameText.getText().toString(), Integer.parseInt(ageText.getText().toString()));
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new List(), null).commit();
            }
        });
        return rootView;
    }

}