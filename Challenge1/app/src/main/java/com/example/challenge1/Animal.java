package com.example.challenge1;

import java.io.Serializable;

public class Animal implements Serializable {
    private  String owner, name;
    private Integer age,image;

    public Animal(String owner, String name, int image, Integer age) {
        if (age<0)
            this.age = 0;
        else
            this.age=age;

        this.owner = owner;
        this.name = name;
        this.image = image;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImagePath(int imageID) {
        this.image = imageID;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        if (age<0)
            this.age = 0;
        else
            this.age=age;
    }
}
