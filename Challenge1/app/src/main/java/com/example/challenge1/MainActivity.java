package com.example.challenge1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Animal> animals;
    private Integer indexSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animals = new ArrayList<>();
        animals.add(new Animal("Joao", "Rhino", R.drawable.rhino, 200));
        animals.add(new Animal("Maria", "Frog", R.drawable.frog, 3));
        animals.add(new Animal("Jose", "Snail", R.drawable.snail, 2));
        indexSelected = 0;

        getSupportFragmentManager().beginTransaction().add(R.id.main_layout, new List(), null).commit();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected ArrayList<Animal> getAnimals() {
        return animals;
    }

    protected void setIndexSelected(Integer index) {
        this.indexSelected = index;
    }

    protected Integer getIndexSelected() {
        return this.indexSelected;
    }

    protected void setAnimal(Integer index, String owner, String name, Integer age) {
        if (owner.length() != 0) {
            this.animals.get(index).setOwner(owner);
        }
        if (name.length() != 0) {
            this.animals.get(index).setName(name);
        }

        this.animals.get(index).setAge(age);
    }

    protected Animal getAnimal(Integer index) {
        return this.animals.get(index);
    }


}

