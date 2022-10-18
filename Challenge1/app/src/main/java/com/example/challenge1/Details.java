package com.example.challenge1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Details extends Fragment {
    AnimalsViewModel viewModel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(AnimalsViewModel.class);


        Animal animalSelected = viewModel.getAnimalSelected();

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

                Animal aux = viewModel.getAnimalSelected();
                String owner = ownerText.getText().toString();
                String name = nameText.getText().toString();
                String age = ageText.getText().toString();

                if (owner.length() > 0) {
                    aux.setOwner(owner);
                }
                if (name.length() > 0) {
                    aux.setName(name);
                }
                try {
                    aux.setAge(Integer.parseInt(ageText.getText().toString()));
                } catch (Exception ignored) {
                }

                viewModel.setAnimalSelected(aux);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new List(), null).commit();
            }
        });
        return rootView;
    }

}