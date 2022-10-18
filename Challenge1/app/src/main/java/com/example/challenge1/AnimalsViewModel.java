package com.example.challenge1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class AnimalsViewModel extends ViewModel {
    private Integer indexSelected;
    private ArrayList<Animal> animals;

    public ArrayList<Animal> getAnimals(){
        if(animals==null){
            animals=new ArrayList<>();
            animals.add(new Animal("Joao", "Rhino", R.drawable.rhino, 200));
            animals.add(new Animal("Maria", "Frog", R.drawable.frog, 3));
            animals.add(new Animal("Jose", "Snail", R.drawable.snail, 2));
        }
        return animals;
    }

    public Integer getIndexSelected(){
        if(indexSelected==null){
            indexSelected=0;
        }
        return indexSelected;
    }

    public Animal getAnimalSelected(){
        return this.animals.get(this.indexSelected);
    }

    public void setIndexSelected(Integer indexSelected){
        this.indexSelected=indexSelected;
    }

    public void setAnimalSelected(Animal animal){
        this.animals.set(indexSelected,animal);
    }

}
