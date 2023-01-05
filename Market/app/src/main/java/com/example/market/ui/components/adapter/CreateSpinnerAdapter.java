package com.example.market.ui.components.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.market.R;
import com.example.market.marketDatabase.Category;

import java.util.ArrayList;


public class CreateSpinnerAdapter extends ArrayAdapter<Category> {

    public CreateSpinnerAdapter(Context context, ArrayList<Category> categories) {
        super(context, 0, categories);
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
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_view_category2, parent, false
            );
        }
        TextView nameView = convertView.findViewById(R.id.category_name2);
        Category currentItem = getItem(position);
        if (currentItem != null) {
            String lang = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
            if (lang.equals("pt")) {
                nameView.setText(currentItem.getNamePT());
            } else {
                nameView.setText(currentItem.getName());
            }
        }
        return convertView;
    }
}