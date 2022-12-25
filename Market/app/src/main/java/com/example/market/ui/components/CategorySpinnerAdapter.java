package com.example.market.ui.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.market.R;
import com.example.market.marketDatabase.Category;

import java.util.ArrayList;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    public CategorySpinnerAdapter(Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        Category category=getItem(position);
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view_category, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.category_name);
        Category currentItem = getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            textViewName.setText(currentItem.getName());
        }
        return convertView;
    }
}
