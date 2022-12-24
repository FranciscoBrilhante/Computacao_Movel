package com.example.market.marketDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Calendar;

@Entity(tableName = "product_table")
public class Product {

    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "category")
    private int category;

    @ColumnInfo(name= "price")
    private Double price;

    @ColumnInfo(name="profile")
    private int profile;

    @ColumnInfo(name = "date")
    private Calendar date;

    @ColumnInfo(name = "images")
    private ArrayList<String> images;

    @ColumnInfo(name = "categoryName")
    private String categoryName;

    @ColumnInfo(name = "profileName")
    private String profileName;

    public Product(int id, String title, String description, int category, Double price, int profile, Calendar date, ArrayList<String> images, String categoryName, String profileName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.profile = profile;
        this.date = date;
        this.images = images;
        this.categoryName=categoryName;
        this.profileName=profileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
